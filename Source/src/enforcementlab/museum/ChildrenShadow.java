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
 * A proxy that adds a guard every time a child gets in, and removes a guard
 * every time a child gets out.
 * @author Rania Taleb
 *
 */
public class ChildrenShadow extends UniformProcessor
{
	/**
	 * The name of this proxy.
	 */
	public static final transient String NAME = "Children shadow";

	/**
	 * A counter keeping track of the number of guards currently in the museum at
	 * any point in time.
	 */
	protected int m_numGuards;

	/**
	 * Creates a new instance of the proxy.
	 */
	public ChildrenShadow()
	{
		super(1, 1);
		m_numGuards = 0;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event in_e = (Event) inputs[0];
		MultiTraceElement mte = new MultiTraceElement();
		if (in_e.equals(CHILD_IN))
		{
			mte.add(new MultiEvent(Event.getAdded(GUARD_IN.getLabel())));
			mte.add(new MultiEvent(in_e));
		}
		else if (in_e.equals(CHILD_OUT))
		{
			mte.add(new MultiEvent(in_e));
			//mte.add(new MultiEvent(Event.getAdded(GUARD_OUT.getLabel())));
		}
		/*else if (in_e.equals(GUARD_IN))
		{
			mte.add(new MultiEvent(Event.getDeleted(GUARD_IN.getLabel())));
		}*/
		else if (in_e.equals(GUARD_OUT))
		{
			mte.add(new MultiEvent(Event.getDeleted(GUARD_OUT.getLabel())));
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
	}

	@Override
	public ChildrenShadow duplicate(boolean with_state)
	{
		ChildrenShadow dp = new ChildrenShadow();
		if (with_state)
		{
			dp.m_numGuards = m_numGuards;
		}
		return dp;
	}
}
