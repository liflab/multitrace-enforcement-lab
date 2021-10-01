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
import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.cep.enforcement.MultiTraceElement;

import static enforcementlab.museum.MuseumSource.CHILD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_OUT;

/**
 * A proxy acting on the museum scenario. The proxy can act on the trace in
 * two ways:
 * <ol>
 * <li>If a child enters, the proxy may first insert a "guard in" event, OR
 * delete the "child in" event</li>
 * <li>If a guard exits, the proxy may delete the "guard out" event</li>
 * </ol>
 * All other events are left unmodified.
 */
public class MuseumProxy extends UniformProcessor
{
	/**
	 * The name of this proxy.
	 */
	public static final transient String NAME = "Museum proxy";
	
	/**
	 * Creates a new instance of the proxy.
	 */
	public MuseumProxy()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event in_e = (Event) inputs[0];
		MultiTraceElement mte = new MultiTraceElement();
		if (in_e.equals(CHILD_IN))
		{
			// Before a child enters, may enter a guard before OR
			// prevent the child from entering
			mte.add(new MultiEvent(GUARD_IN, Event.EPSILON));
			mte.add(new MultiEvent(CHILD_IN, Event.EPSILON));
		}
		else if (in_e.equals(GUARD_OUT))
		{
			// May prevent a guard from leaving
			mte.add(new MultiEvent(GUARD_OUT, Event.EPSILON));
		}
		else
		{
			mte.add(new MultiEvent(in_e));
		}
		outputs[0] = mte;
		return true;
	}

	@Override
	public MuseumProxy duplicate(boolean with_state)
	{
		return new MuseumProxy();
	}
}
