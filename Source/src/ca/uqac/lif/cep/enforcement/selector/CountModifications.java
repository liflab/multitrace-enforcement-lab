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
package ca.uqac.lif.cep.enforcement.selector;

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.Event.Added;
import ca.uqac.lif.cep.enforcement.Event.Deleted;

/**
 * Ranking processor that assigns a score to a trace based on the number of
 * inserted and deleted events it contains.
 */
public class CountModifications extends UniformProcessor
{
	/**
	 * The current score
	 */
	protected int m_score;
	
	/**
	 * Creates a new instance of the processor.
	 */
	public CountModifications()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		if (inputs[0] instanceof Added || inputs[0] instanceof Deleted)
		{
			m_score--;
		}
		outputs[0] = m_score;
		return true;
	}

	@Override
	public CountModifications duplicate(boolean with_state)
	{
		CountModifications c = new CountModifications();
		if (with_state)
		{
			c.m_score = m_score;
		}
		return c;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_score = 0;
	}

}
