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
package enforcementlab.museum;

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.Event;

import static enforcementlab.museum.MuseumSource.ADULT_IN;
import static enforcementlab.museum.MuseumSource.ADULT_OUT;
import static enforcementlab.museum.MuseumSource.CHILD_IN;
import static enforcementlab.museum.MuseumSource.CHILD_OUT;
import static enforcementlab.museum.MuseumSource.GUARD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_OUT;

public abstract class MuseumScore extends UniformProcessor
{
	/**
	 * The number of adults inside the museum in the trace prefix generated
	 * so far.
	 */
	protected int m_numAdults;
	
	/**
	 * The number of children inside the museum in the trace prefix generated
	 * so far.
	 */
	protected int m_numChildren;
	
	/**
	 * The number of guards inside the museum in the trace prefix generated
	 * so far.
	 */
	protected int m_numGuards;
	
	/**
	 * The current score.
	 */
	protected int m_score;
	
	public MuseumScore()
	{
		super(1, 1);
		m_numAdults = 0;
		m_numChildren = 0;
		m_numGuards = 0;
		m_score = 0;
	}
	
	/**
	 * Updates the count of children, adults and guards based on the selected
	 * output event. 
	 * @param e The event
	 */
	protected void updateCounts(Event e)
	{
		if (e.equals(ADULT_IN))
		{
			m_numAdults++;
		}
		else if (e.equals(ADULT_OUT))
		{
			m_numAdults--;
		}
		else if (e.equals(CHILD_IN))
		{
			m_numChildren++;
		}
		else if (e.equals(CHILD_OUT))
		{
			m_numChildren--;
		}
		else if (e.equals(GUARD_IN))
		{
			m_numGuards++;
		}
		else if (e.equals(GUARD_OUT))
		{
			m_numGuards--;
		}
	}
	
	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event e = (Event) inputs[0];
		updateCounts(e);
		updateScore(e);
		outputs[0] = m_score;
		return true;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_numAdults = 0;
		m_numChildren = 0;
		m_numGuards = 0;
		m_score = 0;
	}
	
	protected void copyInto(MuseumScore m, boolean with_state)
	{
		if (with_state)
		{
			m.m_numAdults = m_numAdults;
			m.m_numChildren = m_numChildren;
			m.m_numGuards = m_numGuards;
			m.m_score = m_score;
		}
	}
	
	protected abstract void updateScore(Event e);
}
