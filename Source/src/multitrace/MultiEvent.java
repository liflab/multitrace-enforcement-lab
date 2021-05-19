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
package multitrace;

import java.util.HashSet;

/**
 * A set of atomic events. Two multi-events are equal if they contain
 * the same atomic events.
 */
public class MultiEvent extends HashSet<Event>
{
	/**
	 * Dummy UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a multi-event with a given list of atomic events.
	 * @param events The atomic events
	 */
	public MultiEvent(Event ... events)
	{
		super();
		for (Event e : events)
		{
			add(e);
		}
	}
	
	/**
	 * Creates a multi-event with a given list of atomic event labels.
	 * @param events The atomic event labels
	 */
	public MultiEvent(String ... labels)
	{
		super();
		for (String label : labels)
		{
			add(Event.get(label));
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof MultiEvent))
		{
			return false;
		}
		MultiEvent me = (MultiEvent) o;
		if (me.size() != size())
		{
			return false;
		}
		for (Event e : me)
		{
			if (!contains(e))
			{
				return false;
			}
		}
		return true;
	}
}
