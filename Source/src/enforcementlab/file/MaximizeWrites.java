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
package enforcementlab.file;

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.Event;

/**
 * A processor that gives a score to a trace according to the number of
 * "read" events it contains.
 */
public class MaximizeWrites extends UniformProcessor
{
	/**
	 * The name of this scoring processor.
	 */
	public static final transient String NAME = "Maximize writes";
	
	/**
	 * The current score.
	 */
	protected int m_score;
	
	/**
	 * Creates a new instance of the scoring processor.
	 */
	public MaximizeWrites()
	{
		super(1, 1);
		m_score = 0;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event e = (Event) inputs[0];
		if (e.getLabel().startsWith("Write"))
		{
			m_score++;
		}
		outputs[0] = m_score;
		return true;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_score = 0;
	}

	@Override
	public MaximizeWrites duplicate(boolean with_state)
	{
		MaximizeWrites mr = new MaximizeWrites();
		if (with_state)
		{
			mr.m_score = m_score;
		}
		return mr;
	}
}
