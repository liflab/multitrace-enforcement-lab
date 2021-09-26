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
import java.util.Collection;

public class PrefixTreeElement extends ArrayList<MultiEvent>
{

	/**
	 * Dummy UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a multi-trace element with a given list of multi-events.
	 * @param events The multi-events
	 */
	public PrefixTreeElement(MultiEvent ... events)
	{
		super();
		for (MultiEvent e : events)
		{
			add(e);
		}
	}
	
	/**
	 * Creates a multi-trace element with a given collection of multi-events.
	 * @param events The multi-events
	 */
	public PrefixTreeElement(Collection<MultiEvent> events)
	{
		super();
		for (MultiEvent e : events)
		{
			add(e);
		}
	}
	
	public int getSize()
	{
		int size = 0;
		for (MultiEvent me : this)
		{
			size += me.size();
		}
		return size;
	}

}
