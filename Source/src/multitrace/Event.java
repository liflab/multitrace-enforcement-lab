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

import java.util.HashMap;
import java.util.Map;

/**
 * An atomic event made of a single string.
 */
public class Event 
{
	/**
	 * The empty event.
	 */
	public static final transient Event EPSILON = new Event("");
	
	/**
	 * The label of the event.
	 */
	protected final String m_label;
	
	/**
	 * A pool of cached uni-events.
	 */
	protected static final transient Map<String,Event> s_pool = new HashMap<String,Event>();
	
	/**
	 * Gets an instance of event with given label. This method should be used to
	 * get event instances, to avoid a proliferation of distinct instances with
	 * the same label. 
	 * @param label The label
	 * @return The event
	 */
	public static Event get(String label)
	{
		if (s_pool.containsKey(label))
		{
			return s_pool.get(label);
		}
		Event e = new Event(label);
		s_pool.put(label, e);
		return e;
	}

	/**
	 * Creates a new atomic event.
	 * @param label The label of the evet
	 */
	protected Event(String label)
	{
		super();
		m_label = label;
	}
	
	/**
	 * Returns the label for this event
	 * @return The label
	 */
	public String getLabel()
	{
		return m_label;
	}

	@Override
	public String toString()
	{
		return m_label;
	}
	
	@Override
	public int hashCode()
	{
		return m_label.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof Event))
		{
			return false;
		}
		return ((Event) o).m_label.compareTo(m_label) == 0;
	}

}
