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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.tmf.SinkLast;

/**
 * Processor filtering out a trace of multi-events, in order to keep only
 * uni-projections that do not produce the <tt>false</tt> verdict according
 * to an underlying (uni-)monitor.
 */
public class MultiMonitor extends SynchronousProcessor
{
	/**
	 * The monitor evaluating the validity of an input uni-trace.
	 */
	protected StateMooreMachine m_monitor;
	
	/**
	 * A map associating instances of the monitor with their current state.
	 */
	protected Map<Integer,StateMooreMachine> m_stateMonitors;
	
	/**
	 * Creates a new instance of the multi-monitor.
	 * @param monitor The underlying monitor deciding which traces are valid
	 */
	public MultiMonitor(StateMooreMachine monitor)
	{
		super(1, 1);
		m_monitor = monitor;
		m_stateMonitors = new HashMap<Integer,StateMooreMachine>();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		MultiEvent me_in = (MultiEvent) inputs[0];
		List<Event> evts = new ArrayList<Event>(me_in.size());
		Map<Integer,StateMooreMachine> new_state_monitors = new HashMap<Integer,StateMooreMachine>();
		for (Event e : evts)
		{
			for (StateMooreMachine mm : m_stateMonitors.values())
			{
				StateMooreMachine mm_dup = mm.duplicate(true);
				SinkLast sink = new SinkLast();
				Connector.connect(mm_dup, sink);
				mm_dup.getPushableInput().push(e);
				int state = mm_dup.getCurrentState();
				if (!new_state_monitors.containsKey(state))
				{
					new_state_monitors.put(state, mm_dup);
					Object[] out = sink.getLast();
					if (out == null)
					{
						continue;
					}
					boolean verdict = (Boolean) out[0];
					if (verdict)
					{
						evts.add(e);						
					}
				}
			}
		}
		MultiEvent me_out = new MultiEvent(evts);
		outputs.add(new Object[] {me_out});
		m_stateMonitors = new_state_monitors;
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// No need for this at the moment
		throw new UnsupportedOperationException("Duplication not supported on this processor");
	}
}
