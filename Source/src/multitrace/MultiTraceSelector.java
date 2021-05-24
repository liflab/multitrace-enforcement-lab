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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;

/**
 * Processor that receives a stream of multi-trace elements, and outputs a
 * uni-event projection of that stream, by picking uni-events based on the
 * score given to each uni-trace by an underlying processor.
 */
public abstract class MultiTraceSelector extends SynchronousProcessor
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
	 * The enforcement pipeline of which this selector is part of, if any.
	 */
	protected EnforcementPipeline m_outerPipeline;
	
	/**
	 * The score obtained by the output trace so far.
	 */
	protected float m_score;
	
	/**
	 * The interval at which to decide on an output event (default: 1).
	 */
	protected int m_interval;
	
	/**
	 * A counter to keep track of the number of events output so far.
	 */
	protected int m_eventCount;
	
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
		m_score = 0;
		m_interval = 1;
		m_eventCount = 0;
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
	
	/**
	 * Gets the score obtained by the output trace so far.
	 * @return The score
	 */
	public float getScore()
	{
		return m_score;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_prefix.clear();
		m_pending.clear();
		m_score = 0;
		m_eventCount = 0;
	}
	
	/**
	 * Sets the interval at which to decide on an output event.
	 * @param interval The interval
	 */
	public void setInterval(int interval)
	{
		m_interval = interval;
	}

	/**
	 * Determines if a part of the buffered multi-trace should be processed and
	 * output events be produced.
	 * @return <tt>true</tt> if the selector is ready to select output events
	 */
	protected boolean decide()
	{
		boolean b = false;
		if (m_eventCount % m_interval == 0)
		{
			b = true;
		}
		m_eventCount++;
		return b;
	}
}
