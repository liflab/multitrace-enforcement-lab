/*
    A benchmark for multi-trace runtime enforcement in BeepBeep 3
    Copyright (C) 2021 Laboratoire d'informatique formelle

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.enforcement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.enforcement.Event.Deleted;
import ca.uqac.lif.cep.tmf.BlackHole;
import ca.uqac.lif.cep.tmf.QueueSink;

public class Gate extends SynchronousProcessor
{
	/**
	 * The monitor used in the pipeline.
	 */
	protected Processor m_mu;
	
	/**
	 * The state of the monitor in the last checkpoint.
	 */
	protected Processor m_muCheckpoint;
	
	/**
	 * The processor turning the proxy's output into prefix tree elements.
	 */
	protected Proxy m_proxy;
	
	/**
	 * The processor filtering the sequence of prefix tree elements.
	 */
	protected Filter m_filter;
	
	/**
	 * The processor choosing the trace to output based on the values produced
	 * by the ranking processor.
	 */
	protected Selector m_selector;
	
	/**
	 * A sink to accumulate the outputs from the selector.
	 */
	protected QueueSink m_sink;
	
	/**
	 * A black hole to receive the output of the monitor checkpoint.
	 */
	protected BlackHole m_hole;
	
	/**
	 * An endpoint to evaluate the input trace by the monitor, in order to
	 * determine if it is valid.
	 */
	protected Endpoint<Event,Quadrilean.Value> m_monitorEndpoint;
	
	/**
	 * An instance of {@link Pushable} to send events to the enforcement
	 * pipeline.
	 */
	protected Pushable m_pushable;
	
	/**
	 * A prefix of events received and buffered by the gate.
	 */
	protected List<Event> m_prefix;
	
	/**
	 * A set of objects implementing the {@link Checkpointable} interface, and
	 * which will be notified whenever the gate produces output events.
	 */
	protected Set<Checkpointable> m_toNotify;
	
	/**
	 * The number of times the gate has switched to the enforcement pipeline.
	 */
	protected int m_enforcementSwitches = 0;
	
	public Gate(Processor mu, Proxy p, Filter f, Selector s)
	{
		super(1, 1);
		m_mu = mu;
		m_monitorEndpoint = new Endpoint<Event,Quadrilean.Value>(mu);
		m_proxy = p;
		m_filter = f;
		m_selector = s;
		m_toNotify = new HashSet<Checkpointable>();
		m_sink = new QueueSink();
		m_pushable = m_proxy.getPushableInput();
		Connector.connect(m_proxy, m_filter);
		Connector.connect(m_filter, m_selector);
		Connector.connect(m_selector, m_sink);
		m_prefix = new ArrayList<Event>();
		m_muCheckpoint = m_mu.duplicate(true);
		m_hole = new BlackHole();
		Connector.connect(m_muCheckpoint, m_hole);
	}
	
	/**
	 * Instructs the gate that an external object must be notified of every event
	 * it outputs.
	 * @param c The object to notify
	 */
	public void notify(Checkpointable c)
	{
		m_toNotify.add(c);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		Event in_event = (Event) inputs[0];
		Quadrilean.Value verdict = m_monitorEndpoint.getLastValue(in_event);
		m_prefix.add(in_event);
		if (verdict == Quadrilean.Value.TRUE || verdict == Quadrilean.Value.P_TRUE)
		{
			// Property is satisfied: let the event through
			for (Event e : m_prefix)
			{
				outputs.add(new Object[] {e});
			}
			m_proxy.apply(m_prefix);
			m_filter.apply(m_prefix);
			m_selector.apply(m_prefix);
			for (Checkpointable c : m_toNotify)
			{
				c.apply(m_prefix);
			}
			m_prefix.clear();
			m_muCheckpoint = m_monitorEndpoint.m_processor.duplicate(true);
			Connector.connect(m_muCheckpoint, m_hole);
			return true;
		}
		// Wait for the enforcement pipeline to output something
		m_pushable.push(in_event);
		Queue<Object> q = m_sink.getQueue();
		if (!q.isEmpty())
		{
			// The pipeline produced a sequence: output it
			m_enforcementSwitches++;
			List<Event> to_output = new ArrayList<Event>();
			while (!q.isEmpty())
			{
				to_output.add((Event) q.remove());
			}
			Pushable p = m_muCheckpoint.getPushableInput();
			for (Event out_event : to_output)
			{
				outputs.add(new Object[] {out_event});
				if (out_event != null && !out_event.getLabel().isEmpty() && !(out_event instanceof Deleted))
				{
					p.push(out_event);
				}
			}
			m_proxy.apply(to_output);
			m_filter.apply(to_output);
			m_selector.apply(to_output);
			for (Checkpointable c : m_toNotify)
			{
				c.apply(to_output);
			}
			m_monitorEndpoint =  new Endpoint<Event,Quadrilean.Value>(m_muCheckpoint.duplicate(true));
			m_prefix.clear();
		}
		return true;
	}
	
	public int getEnforcementSwitches()
	{
		return m_enforcementSwitches;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_enforcementSwitches = 0;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// Not needed at the moment
		return null;
	}
}
