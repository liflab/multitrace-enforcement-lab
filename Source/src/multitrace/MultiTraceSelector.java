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
package multitrace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.tmf.BlackHole;

/**
 * Processor that receives a stream of multi-events, and outputs a uni-event
 * projection of that stream, by picking uni-events based on the score given
 * to each uni-trace by an underlying processor.
 */
public class MultiTraceSelector extends SynchronousProcessor
{
	/**
	 * The trace of uni-events sent to the output so far.
	 */
	protected List<Event> m_prefix;

	/**
	 * The ordered sequence of multi-trace elements that have not yet been processed.
	 */
	protected List<MultiTraceElement> m_pending;

	/**
	 * A processor that produces a score ranking uni-traces.
	 */
	protected Processor m_monitor;

	/**
	 * A {@link Pushable} to push events to the ranking processor.
	 */
	protected Pushable m_pushable;

	/**
	 * The enforcement pipeline of which this selector is part of, if any.
	 */
	protected EnforcementPipeline m_outerPipeline;

	/**
	 * Creates a new instance of selector.
	 * @param monitor A processor that produces a score ranking uni-traces.
	 * Currently, this processor must be 1:1, accept {@link MultiEvents} as
	 * its input and produce integers as its output.
	 */
	public MultiTraceSelector(Processor monitor)
	{
		super(1, 1);
		m_monitor = monitor;
		m_prefix = new ArrayList<Event>();
		m_pending = new ArrayList<MultiTraceElement>();
		m_pushable = m_monitor.getPushableInput();
		BlackHole hole = new BlackHole();
		Connector.connect(m_monitor, hole);
	}

	/**
	 * Notifies the processor of the enforcement pipeline it is
	 * integrated in
	 * @param p The enforcement pipeline
	 */
	public void setEnforcementPipeline(EnforcementPipeline p)
	{
		m_outerPipeline = p;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException
	{
		MultiTraceElement mte = (MultiTraceElement) inputs[0];
		m_pending.add(mte);
		if (!decide())
		{
			// Accumulate but output nothing
			return true;
		}
		List<EventEndpoint<Integer>> endpoints = new ArrayList<EventEndpoint<Integer>>();
		endpoints.add(new EventEndpoint<Integer>(m_monitor.duplicate(true)));
		List<EventEndpoint<Integer>> new_endpoints = new ArrayList<EventEndpoint<Integer>>();
		Iterator<MultiTraceElement> it = m_pending.iterator();
		while (it.hasNext())
		{
			MultiTraceElement t_me = it.next();
			it.remove();
			for (int j = 0; j < t_me.size(); j++)
			{
				EventEndpoint<Integer> ep = endpoints.get(j);
				MultiEvent me = t_me.get(j);
				for (int i = 0; i < me.size(); i++)
				{
					Event e = me.get(i);
					EventEndpoint<Integer> n_ep = ep.duplicate();
					n_ep.getVerdict(e);
					new_endpoints.add(n_ep);
				}
			}
			endpoints = new_endpoints;
		}
		// Find the endpoint with the highest score
		int best_score = Integer.MIN_VALUE;
		Endpoint<Event,Integer> best_endpoint = null;
		for (Endpoint<Event,Integer> ep : endpoints)
		{
			int score = ep.getLastValue();
			if (score > best_score)
			{
				score = best_score;
				best_endpoint = ep;
			}
		}
		if (best_endpoint != null)
		{
			List<Event> to_output = best_endpoint.getInputTrace();
			m_prefix.addAll(to_output);
			// The sequence of uni-events to produce has been computed
			for (Event e : to_output)
			{
				// Output this best event, if it is not the empty event
				if (!e.getLabel().isEmpty())
				{
					outputs.add(new Object[] {e});
					m_pushable.push(e);
				}
			}
			// Notify the enforcement pipeline that events have been output
			if (m_outerPipeline != null && !to_output.isEmpty())
			{
				m_outerPipeline.apply(to_output);
			}
		}
		return true;
	}
	
	@Override
	public MultiTraceSelector duplicate(boolean with_state) 
	{
		MultiTraceSelector s = new MultiTraceSelector(m_monitor.duplicate(with_state));
		if (with_state)
		{
			s.m_prefix.addAll(m_prefix);
			s.m_pending.addAll(m_pending);
		}
		return s;
	}

	@Override
	public void reset()
	{
		super.reset();
		m_monitor.reset();
		m_prefix.clear();
		m_pending.clear();
	}

	/**
	 * Determines if a part of the buffered multi-trace should be processed and
	 * output events be produced.
	 * @return <tt>true</tt> if the selector is ready to select output events
	 */
	protected boolean decide()
	{
		return true;
	}
}
