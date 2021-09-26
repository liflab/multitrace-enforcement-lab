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

import ca.uqac.lif.cep.UniformProcessor;

/**
 * Processor that gives as score for a trace the sum of ASCII value of all
 * events passed to it. This processor has no particular meaning and is used
 * for testing. For a given multi-event, a selector using this monitor should
 * always prefer the uni-event with the highest ASCII value.
 */
public class HighestString extends UniformProcessor
{
	/**
	 * The total score received so far
	 */
	protected int m_total;
	
	/**
	 * Creates a new instance of the processor.
	 */
	protected HighestString()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] input, Object[] output)
	{
		String label = ((Event) input[0]).getLabel();
		if (!label.isEmpty())
		{
			m_total += label.charAt(0);
		}
		output[0] = m_total;
		return true;
	}

	@Override
	public HighestString duplicate(boolean with_state)
	{
		HighestString hs = new HighestString();
		hs.m_total = m_total;
		return hs;
	}
	
	@Override
	public void reset()
	{
		m_total = 0;
	}
}
