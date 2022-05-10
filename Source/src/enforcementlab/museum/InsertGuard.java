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
 * A proxy that inserts a guard whenever a child enters a museum with no guard
 * inside, or when the last guard gets out and children are still in the
 * museum. Otherwise, the input events are let through without modification.
 * @author Rania Taleb
 *
 */
public class InsertGuard extends UniformProcessor
{
	/**
	 * The name of this proxy.
	 */
	public static final transient String NAME = "Insert guard";
	
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
	public InsertGuard()
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
			if (m_numChildren == 0 || m_numGuards > 1)
			{
				m_numGuards--;
				mte.add(new MultiEvent(in_e));
			}
			else
			{
				mte.add(new MultiEvent(Event.getDeleted(GUARD_OUT.getLabel())));
			}
		}
		else if (in_e.equals(CHILD_IN))
		{
			m_numChildren++;
			if (m_numGuards > 0)
			{
				mte.add(new MultiEvent(CHILD_IN));	
			}
			else
			{
				// Insert guard
				m_numGuards++;
				mte.add(new MultiEvent(Event.getAdded(GUARD_IN.getLabel())));
				mte.add(new MultiEvent(in_e));
			}
		}
		else if (in_e.equals(CHILD_OUT))
		{
			m_numChildren--;
			mte.add(new MultiEvent(in_e));
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
	public InsertGuard duplicate(boolean with_state)
	{
		InsertGuard dp = new InsertGuard();
		if (with_state)
		{
			dp.m_numGuards = m_numGuards;
			dp.m_numChildren = m_numChildren;
		}
		return dp;
	}
}
