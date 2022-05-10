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
import static enforcementlab.museum.MuseumSource.GUARD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_OUT;

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.cep.enforcement.MultiTraceElement;

/**
 * A proxy that inserts a guard whenever a child enters a museum with no guard
 * inside. Contrary to {@link InsertGuard}, which keeps an exact count of the
 * guards in the museum, this proxy only maintains a Boolean flag, which is
 * set to <tt>true</tt> when a guard enters, and to <tt>false</tt> when a guard
 * gets out. The same is done for children.
 * @author Rania Taleb
 *
 */
public class InsertGuardNoCount extends UniformProcessor
{
	/**
	 * The name of this proxy.
	 */
	public static final transient String NAME = "Insert guard (no count)";
	
	/**
	 * A flag keeping a conservative approximation of whether there are guards in
	 * the museum.
	 */
	protected boolean m_hasGuards;
	
	/**
	 * A flag keeping a conservative approximation of whether there are guards in
	 * the museum.
	 */
	protected boolean m_hasChildren;
	
	/**
	 * Creates a new instance of the proxy.
	 */
	public InsertGuardNoCount()
	{
		super(1, 1);
		m_hasGuards = false;
		m_hasChildren = false;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event in_e = (Event) inputs[0];
		MultiTraceElement mte = new MultiTraceElement();
		if (in_e.equals(GUARD_IN))
		{
			m_hasGuards = true;
			mte.add(new MultiEvent(in_e));
		}
		else if (in_e.equals(GUARD_OUT))
		{
			if (m_hasChildren)
			{
				// Delete guard out
				mte.add(new MultiEvent(Event.getDeleted(GUARD_OUT.getLabel())));
			}
			else
			{
				m_hasGuards = false;
				mte.add(new MultiEvent(in_e));
			}
		}
		else if (in_e.equals(CHILD_IN))
		{
			m_hasChildren = true;
			if (m_hasGuards)
			{
				mte.add(new MultiEvent(in_e));	
			}
			else
			{
				// Insert guard
				m_hasGuards = true;
				mte.add(new MultiEvent(Event.getAdded(GUARD_IN.getLabel())));
				mte.add(new MultiEvent(in_e));
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
		m_hasGuards = false;
		m_hasChildren = false;
	}

	@Override
	public InsertGuardNoCount duplicate(boolean with_state)
	{
		InsertGuardNoCount dp = new InsertGuardNoCount();
		if (with_state)
		{
			dp.m_hasGuards = m_hasGuards;
			dp.m_hasChildren = m_hasChildren;
		}
		return dp;
	}
}
