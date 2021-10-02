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
package enforcementlab.abc;

import static org.junit.Assert.*;

import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.Gate;
import ca.uqac.lif.cep.enforcement.IntervalFilter;
import ca.uqac.lif.cep.enforcement.IntervalSelector;
import ca.uqac.lif.cep.enforcement.Proxy;
import ca.uqac.lif.cep.enforcement.proxy.InsertAny;
import ca.uqac.lif.cep.enforcement.selector.CountModifications;
import ca.uqac.lif.cep.tmf.QueueSink;

/**
 * Integration tests of the whole pipeline for the a-b-c scenario.
 */
public class PipelineTest
{
	public static final Event A = Event.get("a");
	public static final Event B = Event.get("b");
	public static final Event C = Event.get("c");
	
	@Test
	public void test1()
	{
		int interval = 2;
		Event e;
		Gate g = new Gate(new Property1(), 
				new Proxy(new InsertAny(AbcSource.getAlphabet())), 
				new IntervalFilter(new Property1(), 2), 
				new IntervalSelector(new CountModifications(), interval));
		QueueSink sink = new QueueSink();
		Connector.connect(g, sink);
		Pushable p = g.getPushableInput();
		Queue<Object> queue = sink.getQueue();
		p.push(C);
		assertFalse(queue.isEmpty());
		assertEquals(C, queue.remove());
		p.push(C);
		assertFalse(queue.isEmpty());
		assertEquals(C, queue.remove());
		p.push(A);
		assertTrue(queue.isEmpty());
		p.push(C);
		assertEquals(3, queue.size());
	}
}
