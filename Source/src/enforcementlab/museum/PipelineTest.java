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

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.Event.Added;
import ca.uqac.lif.cep.enforcement.Gate;
import ca.uqac.lif.cep.enforcement.IntervalFilter;
import ca.uqac.lif.cep.enforcement.IntervalSelector;
import ca.uqac.lif.cep.enforcement.Proxy;
import ca.uqac.lif.cep.enforcement.selector.CountModifications;
import ca.uqac.lif.cep.tmf.QueueSink;

import static enforcementlab.museum.MuseumSource.ADULT_IN;
import static enforcementlab.museum.MuseumSource.ADULT_OUT;
import static enforcementlab.museum.MuseumSource.CHILD_IN;
import static enforcementlab.museum.MuseumSource.CHILD_OUT;
import static enforcementlab.museum.MuseumSource.GUARD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_OUT;
import static org.junit.Assert.*;

import java.util.Queue;

/**
 * Integration tests of the whole pipeline for the museum scenario.
 */
public class PipelineTest
{
	@Test
	public void test1()
	{
		int interval = 3;
		Event e;
		Gate g = new Gate(new MuseumPolicy(), 
				new Proxy(new MuseumProxy()), 
				new IntervalFilter(new MuseumPolicy(), interval), 
				new IntervalSelector(new CountModifications(), interval));
		QueueSink sink = new QueueSink();
		Connector.connect(g, sink);
		Pushable p = g.getPushableInput();
		Queue<Object> queue = sink.getQueue();
		p.push(CHILD_IN);
		assertTrue(queue.isEmpty());
		p.push(ADULT_IN);
		assertFalse(queue.isEmpty());
		e = (Event) queue.remove();
		assertEquals(GUARD_IN, e);
		assertTrue(e instanceof Added);
		e = (Event) queue.remove();
		assertEquals(CHILD_IN, e);
		assertFalse(e instanceof Added);
		e = (Event) queue.remove();
		assertEquals(ADULT_IN, e);
		assertFalse(e instanceof Added);
		assertTrue(queue.isEmpty());
	}

	@Test
	public void test2()
	{
		int interval = 4;
		Event e;
		Gate g = new Gate(new MuseumPolicy(), 
				new Proxy(new MuseumProxy()), 
				new IntervalFilter(new MuseumPolicy(), interval), 
				new IntervalSelector(new CountModifications(), interval));
		QueueSink sink = new QueueSink();
		Connector.connect(g, sink);
		Pushable p = g.getPushableInput();
		Queue<Object> queue = sink.getQueue();
		// Event 1
		p.push(GUARD_IN);
		assertFalse(queue.isEmpty());
		e = (Event) queue.remove();
		assertEquals(GUARD_IN, e);
		assertFalse(e instanceof Added);
		// Event 2
		p.push(GUARD_OUT);
		assertFalse(queue.isEmpty());
		e = (Event) queue.remove();
		assertEquals(GUARD_OUT, e);
		assertFalse(e instanceof Added);
		// Event 3
		p.push(CHILD_IN);
		assertTrue(queue.isEmpty());
		// Event 4
		p.push(GUARD_IN);
		assertTrue(queue.isEmpty());
		// Event 5
		p.push(ADULT_IN);
		assertFalse(queue.isEmpty());
	}
}
