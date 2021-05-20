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
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.tmf.BlackHole;

/**
 * Processor filtering out a trace of multi-events, in order to keep only
 * uni-projections that do not produce the <tt>false</tt> verdict according
 * to an underlying (uni-)monitor.
 */
public class MultiTraceFilter extends SynchronousProcessor
{
	/**
	 * The monitor evaluating the validity of an input uni-trace.
	 */
	protected Processor m_monitor;

	/**
	 * A pushable to push events to the monitor.
	 */
	protected Pushable m_pushable;

	/**
	 * A list of endpoints.
	 */
	protected List<MonitorEndpoint> m_endpoints;

	/**
	 * Creates a new instance of the multi-monitor.
	 * @param monitor The underlying monitor deciding which traces are valid
	 */
	public MultiTraceFilter(Processor monitor)
	{
		super(1, 1);
		m_monitor = monitor;
		BlackHole hole = new BlackHole();
		Connector.connect(m_monitor, hole);
		m_pushable = m_monitor.getPushableInput();
		m_endpoints = new ArrayList<MonitorEndpoint>();
		MonitorEndpoint e = new MonitorEndpoint(m_monitor.duplicate());
		m_endpoints.add(e);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		MultiTraceElement mte_in = (MultiTraceElement) inputs[0];
		MultiTraceElement mte_out = new MultiTraceElement();
		List<MonitorEndpoint> new_endpoints = new ArrayList<MonitorEndpoint>();
		for (int j = 0; j < m_endpoints.size(); j++)
		{
			MonitorEndpoint ep = m_endpoints.get(j);
			MultiEvent me_in = mte_in.get(j);
			List<Event> evts = new ArrayList<Event>();
			for (Event e : me_in)
			{
				MonitorEndpoint n_ep = ep.duplicate();
				boolean verdict = n_ep.getVerdict(e);
				new_endpoints.add(n_ep);
				if (verdict)
				{
					evts.add(e);
				}
			}
			if (!evts.isEmpty())
			{
				MultiEvent me_out = new MultiEvent(evts);
				mte_out.add(me_out);
			}
		}
		outputs.add(new Object[] {mte_out});
		m_endpoints = new_endpoints;
		return true;
	}

	@Override
	public void reset()
	{
		super.reset();
		m_monitor.reset();
		m_endpoints.clear();
		MonitorEndpoint e = new MonitorEndpoint(m_monitor.duplicate());
		m_endpoints.add(e);
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// No need for this at the moment
		throw new UnsupportedOperationException("Duplication not supported on this processor");
	}

	public void apply(List<Event> trace)
	{
		for (Event e : trace)
		{
			m_pushable.push(e);
		}
		m_endpoints.clear();
		MonitorEndpoint e = new MonitorEndpoint(m_monitor.duplicate(true));
		m_endpoints.add(e);
	}
}
