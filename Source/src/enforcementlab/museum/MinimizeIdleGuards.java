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

/**
 * A scoring processor that penalizes traces with idle guards. More precisely,
 * at each event:
 * <ul>
 * <li>if no child is inside the museum at that moment, one unit of score is
 * subtracted for every guard inside the museum</li>
 * <li>if children are inside the museum, one unit of score is subtracted for
 * every guard that exceeds the number of children</li>
 * </ul>
 * The consequence of such a scoring function is to favor traces where as few
 * guards as possible enter the museum, and guards leave as early as possible. 
 */
public class MinimizeIdleGuards extends MuseumScore
{
	/**
	 * A name given to this scoring processor.
	 */
	public static final transient String NAME = "Minimize idle guards";
	
	@Override
	protected void updateScore(Event e)
	{
		if (m_numChildren <= 0 && m_numGuards > 0)
		{
			m_score -= m_numGuards;
		}
	}

	@Override
	public MinimizeIdleGuards duplicate(boolean with_state)
	{
		MinimizeIdleGuards p = new MinimizeIdleGuards();
		copyInto(p, with_state);
		return p;
	}

}
