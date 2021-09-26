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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Wraps an event into a multi-event containing that single event.
 */
public class WrapEvent extends UnaryFunction<Event,MultiEvent>
{
	/**
	 * A single visible instance of the function.
	 */
	public static final transient WrapEvent instance = new WrapEvent();
	
	/**
	 * Creates a new instance of the function.
	 */
	protected WrapEvent()
	{
		super(Event.class, MultiEvent.class);
	}

	@Override
	public WrapEvent duplicate(boolean with_state)
	{
		return this;
	}

	@Override
	public MultiEvent getValue(Event e) 
	{
		List<Event> evts = new ArrayList<Event>();
		evts.add(e);
		MultiEvent me = new MultiEvent(evts);
		return me;
	}

}