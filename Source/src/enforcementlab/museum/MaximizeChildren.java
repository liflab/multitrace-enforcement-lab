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

import ca.uqac.lif.cep.enforcement.Event;

import static enforcementlab.museum.MuseumSource.CHILD_IN;

/**
 * A scoring processor that adds one point of score for every child
 * entering the museum. 
 */
public class MaximizeChildren extends MuseumScore
{
	/**
	 * A name given to this scoring processor.
	 */
	public static final transient String NAME = "Maximize children";
	
	public MaximizeChildren()
	{
		super();
	}
	
	@Override
	protected void updateScore(Event e)
	{
		if (e.equals(CHILD_IN))
		{
			m_score++;
		}
	}

	@Override
	public MaximizeChildren duplicate(boolean with_state)
	{
		MaximizeChildren p = new MaximizeChildren();
		copyInto(p, with_state);
		return p;
	}

}
