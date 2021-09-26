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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.cep.tmf.Passthrough;

public class EventEndpointTest
{
	@Test
	public void test1()
	{
		Event e;
		EventEndpoint<Event> ep = new EventEndpoint<Event>(new Passthrough());
		e = ep.getLastValue(Event.get("a"));
		assertEquals("a", e.getLabel());
		e = ep.getLastValue(Event.getAdded("b"));
		assertEquals("b", e.getLabel());
		e = ep.getLastValue(Event.getDeleted("a"));
		assertEquals("b", e.getLabel());
		List<Event> trace = ep.getInputTrace();
		assertEquals(3, trace.size());
	}
}
