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

import static ca.uqac.lif.cep.enforcement.FilterTest.getList;
import static org.junit.Assert.*;

import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.tmf.SinkLast;

/**
 * Unit tests for the {@link IntervalSelector} processor.
 */
public class SelectorTest
{
	protected static final Event a = Event.get("a");
	protected static final Event b = Event.get("b");
	protected static final MultiEvent A = new MultiEvent(a);
	protected static final MultiEvent B = new MultiEvent(b);
	protected static final MultiEvent AB = new MultiEvent(a, b);
	protected static final MultiEvent eB = new MultiEvent(Event.EPSILON, b);

	@Test
	public void testHighestString1()
	{
		IntervalSelector s = new IntervalSelector(new HighestString(), 1);
		SinkLast sink = new SinkLast();
		Connector.connect(s, sink);
		Event e = null;
		Pushable p = s.getPushableInput();
		p.push(getList(new PrefixTreeElement(A)));
		e = (Event) sink.getLast()[0];
		assertEquals("a", e.getLabel());
		p.push(getList(new PrefixTreeElement(AB)));
		e = (Event) sink.getLast()[0];
		assertEquals("b", e.getLabel());
		p.push(getList(new PrefixTreeElement(AB)));
		e = (Event) sink.getLast()[0];
		assertEquals("b", e.getLabel());
	}

	@Test
	public void testDecideEveryOther1()
	{
		IntervalSelector s = new IntervalSelector(new HighestString(), 2);
		QueueSink sink = new QueueSink();
		Queue<Object> queue = sink.getQueue();
		Connector.connect(s, sink);
		Pushable p = s.getPushableInput();
		p.push(getList(new PrefixTreeElement(A)));
		assertEquals(0, queue.size());
		p.push(getList(new PrefixTreeElement(AB)));
		assertEquals(2, queue.size());
		queue.clear();
		p.push(getList(new PrefixTreeElement(eB, eB)));
		assertEquals(0, queue.size());
	}
}
