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

import static org.junit.Assert.*;

import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.tmf.SinkLast;

/**
 * Unit tests for the {@link Selector} processor.
 */
public class SelectorTest
{
	@Test
	public void testHighestString1()
	{
		Selector s = new Selector(new HighestString());
		SinkLast sink = new SinkLast();
		Connector.connect(s, sink);
		Event e = null;
		Pushable p = s.getPushableInput();
		p.push(new MultiEvent("a"));
		e = (Event) sink.getLast()[0];
		assertEquals("a", e.getLabel());
		p.push(new MultiEvent("a", "b"));
		e = (Event) sink.getLast()[0];
		assertEquals("b", e.getLabel());
		p.push(new MultiEvent("", "b"));
		e = (Event) sink.getLast()[0];
		assertEquals("b", e.getLabel());
	}
	
	@Test
	public void testDecideEveryOther1()
	{
		Selector s = new SelectorEveryTwo(new HighestString());
		QueueSink sink = new QueueSink();
		Queue<Object> queue = sink.getQueue();
		Connector.connect(s, sink);
		Pushable p = s.getPushableInput();
		p.push(new MultiEvent("a"));
		assertEquals(1, queue.size());
		p.push(new MultiEvent("a", "b"));
		assertEquals(1, queue.size());
		p.push(new MultiEvent("", "b"));
		assertEquals(3, queue.size());
	}
	
	/**
	 * Selector that outputs a sequence of events at every other input event.
	 * It has no particular meaning and is only used for testing.
	 */
	protected static class SelectorEveryTwo extends Selector
	{
		protected int m_count = 0;
		
		public SelectorEveryTwo(Processor monitor)
		{
			super(monitor);
		}
		
		@Override
		protected boolean decide()
		{
			return m_count++ % 2 == 0;
		}
	}
}
