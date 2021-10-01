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
import ca.uqac.lif.cep.enforcement.Quadrilean;
import ca.uqac.lif.cep.enforcement.Quadrilean.Value;

import static enforcementlab.museum.MuseumSource.ADULT_IN;
import static enforcementlab.museum.MuseumSource.ADULT_OUT;
import static enforcementlab.museum.MuseumSource.CHILD_IN;
import static enforcementlab.museum.MuseumSource.CHILD_OUT;
import static enforcementlab.museum.MuseumSource.GUARD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_OUT;

/**
 * A monitor for the security policy of the museum scenario. The policy checks
 * two things:
 * <ul>
 * <li>By tracking in/out events, the number of adults, children
 * and guards in the museum at any given time cannot be negative</li>
 * <li>A guard must be in the museum whenever at least one child is in the
 * museum</li>
 * </ul>
 */
public class MuseumPolicy extends UniformProcessor
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
	 * The current verdict.
	 */
	protected Quadrilean.Value m_verdict;
	
	/**
	 * Creates a new instance of the policy monitor.
	 */
	public MuseumPolicy()
	{
		super(1, 1);
		m_numAdults = 0;
		m_numChildren = 0;
		m_numGuards = 0;
		m_verdict = Value.P_TRUE;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_numAdults = 0;
		m_numChildren = 0;
		m_numGuards = 0;
		m_verdict = Value.P_TRUE;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		// If property is violated, stays violated no matter what
		if (m_verdict == Value.FALSE)
		{
			outputs[0] = Value.FALSE;
			return true;
		}
		Event e = (Event) inputs[0];
		if (e.equals(ADULT_IN))
		{
			m_numAdults++;
		}
		else if (e.equals(CHILD_IN))
		{
			m_numChildren++;
			if (m_numGuards <= 0)
			{
				m_verdict = Value.FALSE;
			}
		}
		else if (e.equals(GUARD_IN))
		{
			m_numGuards++;
		}
		else if (e.equals(ADULT_OUT))
		{
			m_numAdults--;
			if (m_numAdults < 0)
			{
				m_verdict = Value.FALSE;
			}
		}
		else if (e.equals(CHILD_OUT))
		{
			m_numChildren--;
			if (m_numChildren< 0)
			{
				m_verdict = Value.FALSE;
			}
		}
		else if (e.equals(GUARD_OUT))
		{
			m_numGuards--;
			if (m_numGuards< 0)
			{
				m_verdict = Value.FALSE;
			}
		}
		outputs[0] = m_verdict;
		return true;
	}

	@Override
	public MuseumPolicy duplicate(boolean with_state)
	{
		MuseumPolicy mp = new MuseumPolicy();
		if (with_state)
		{
			mp.m_numAdults = m_numAdults;
			mp.m_numChildren = m_numChildren;
			mp.m_numGuards = m_numGuards;
			mp.m_verdict = m_verdict;
		}
		return mp;
	}
}
