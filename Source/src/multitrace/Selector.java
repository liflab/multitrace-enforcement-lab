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
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.tmf.SinkLast;

/**
 * Processor that receives a stream of multi-events, and outputs a uni-event
 * projection of that stream, by picking uni-events based on the score given
 * to each uni-trace by an underlying processor.
 */
public class Selector extends SynchronousProcessor
{
	/**
	 * The trace of uni-events sent to the output so far.
	 */
	protected List<Event> m_prefix;
	
	/**
	 * The score produced for the prefix of the trace output so far.
	 */
	protected int m_lastScore;
	
	/**
	 * The ordered sequence of multi-events that have not yet been processed.
	 */
	protected List<MultiEvent> m_pending;
	
	/**
	 * A processor that produces a score ranking uni-traces.
	 */
	protected Processor m_monitor;
	
	/**
	 * Creates a new instance of selector.
	 * @param monitor A processor that produces a score ranking uni-traces.
	 * Currently, this processor must be 1:1, accept {@link MultiEvents} as
	 * its input and produce integers as its output.
	 */
	public Selector(Processor monitor)
	{
		super(1, 1);
		m_monitor = monitor;
		m_prefix = new ArrayList<Event>();
		m_pending = new ArrayList<MultiEvent>();
		m_lastScore = 0;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_monitor.reset();
		m_prefix.clear();
		m_pending.clear();
		m_lastScore = 0;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException
	{
		MultiEvent me = (MultiEvent) inputs[0];
		m_pending.add(me);
		Iterator<MultiEvent> it = m_pending.iterator();
		if (!decide())
		{
			// Accumulate but output nothing
			return true;
		}
		while (it.hasNext())
		{
			MultiEvent t_me = it.next();
			it.remove();
			Event e_best = null;
			Processor p_best = null;
			int s_best = Integer.MIN_VALUE;
			for (Event e : t_me)
			{
				int score;
				// Make a copy of the monitor in its current state
				Processor p_e = m_monitor.duplicate(true);
				if (e.getLabel().isEmpty()) // Empty event
				{
					// Empty event: nothing to push and score does not change
					score = m_lastScore;
				}
				else
				{
					// Non-empty event: push into monitor and get score
					SinkLast qs = new SinkLast();
					Connector.connect(p_e, qs);
					p_e.getPushableInput().push(e);
					Object[] out = qs.getLast();
					if (out == null)
					{
						continue;
					}
					score = (Integer) out[0];
				}
				// If the score produces beats the best score so far...
				if (score > s_best)
				{
					// Keep this event and monitor as the current best
					s_best = score;
					p_best = p_e;
					e_best = e;
				}
			}
			if (p_best == null)
			{
				// Should not happen
				throw new ProcessorException("Monitor produced no score for any of the input events");
			}
			// Add the best event to the prefix
			m_prefix.add(e_best);
			// Output this best event, if it is not the empty event
			if (!e_best.getLabel().isEmpty())
			{
				outputs.add(new Object[] {e_best});
			}
			// Update the new current monitor state 
			m_monitor = p_best;
		}
		return true;
	}

	@Override
	public Selector duplicate(boolean with_state) 
	{
		Selector s = new Selector(m_monitor.duplicate(with_state));
		if (with_state)
		{
			s.m_prefix.addAll(m_prefix);
			s.m_pending.addAll(m_pending);
			s.m_lastScore = m_lastScore;
		}
		return s;
	}
	
	/**
	 * Determines if a part of the buffered multi-trace should be processed and
	 * output events be produced. The default behavior is to return <tt>true</tt>
	 * on every input multi-event received. Override this method to decide based
	 * on a different condition.
	 * @return <tt>true</tt>
	 */
	protected boolean decide()
	{
		return true;
	}
}
