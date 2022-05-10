/*
    A benchmark for multi-trace runtime enforcement in BeepBeep 3
    Copyright (C) 2021-2022 Laboratoire d'informatique formelle

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

import static enforcementlab.museum.MuseumSource.CHILD_IN;
import static enforcementlab.museum.MuseumSource.CHILD_OUT;
import static enforcementlab.museum.MuseumSource.GUARD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_OUT;

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.cep.enforcement.MultiTraceElement;

/**
 * A proxy that deletes all "children in" events when there are no guards in
 * the museum, and expulses all children when the last guard goes out.
 * @author Rania Taleb
 *
 */
public class DeleteChildren extends UniformProcessor
{
	/**
	 * The name of this proxy.
	 */
	public static final transient String NAME = "Delete children";

	/**
	 * A counter keeping track of the number of guards currently in the museum at
	 * any point in time.
	 */
	protected int m_numGuards;

	/**
	 * A counter keeping track of the number of children currently in the museum
	 * at any point in time.
	 */
	protected int m_numChildren;

	/**
	 * Creates a new instance of the proxy.
	 */
	public DeleteChildren()
	{
		super(1, 1);
		m_numGuards = 0;
		m_numChildren = 0;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event in_e = (Event) inputs[0];
		MultiTraceElement mte = new MultiTraceElement();
		if (in_e.equals(GUARD_IN))
		{
			m_numGuards++;
			mte.add(new MultiEvent(in_e));
		}
		else if (in_e.equals(GUARD_OUT))
		{
			if (m_numChildren > 0 && m_numGuards == 1)
			{
				for (int i = 0; i < m_numChildren; i++)
				{
					mte.add(new MultiEvent(Event.getAdded(CHILD_OUT.getLabel())));	
				}
				mte.add(new MultiEvent(in_e));
				m_numGuards = 0;
				m_numChildren = 0;
			}
			else
			{
				m_numGuards--;
				mte.add(new MultiEvent(in_e));
			}
		}
		else if (in_e.equals(CHILD_IN))
		{
			if (m_numGuards > 0)
			{
				mte.add(new MultiEvent(in_e));
				m_numChildren++;
			}
			else
			{
				// Delete child in
				mte.add(new MultiEvent(Event.getDeleted(CHILD_IN.getLabel())));
			}
		}
		else if (in_e.equals(CHILD_OUT))
		{
			if (m_numChildren == 0)
			{
				mte.add(new MultiEvent(Event.getDeleted(CHILD_OUT.getLabel())));
			}
			else
			{
				mte.add(new MultiEvent(in_e));
				m_numChildren--;
			}
		}
		else
		{
			mte.add(new MultiEvent(in_e));
		}
		outputs[0] = mte;
		return true;
	}

	@Override
	public void reset()
	{
		super.reset();
		m_numGuards = 0;
		m_numChildren = 0;
	}

	@Override
	public DeleteChildren duplicate(boolean with_state)
	{
		DeleteChildren dp = new DeleteChildren();
		if (with_state)
		{
			dp.m_numGuards = m_numGuards;
			dp.m_numChildren = m_numChildren;
		}
		return dp;
	}
}
