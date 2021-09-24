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
package multitrace.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.tmf.SinkLast;
import multitrace.Event;
import multitrace.MultiEvent;
import multitrace.PrefixTreeElement;
import multitrace.Proxy;

/**
 * Unit tests for {@link AppendToMultiTrace}.
 */
public class ProxyTest
{
	@SuppressWarnings("unchecked")
	@Test
	public void test1()
	{
		Proxy a = new Proxy(new WrapEvent());
		SinkLast sink = new SinkLast();
		Connector.connect(a, sink);
		Pushable p = a.getPushableInput();
		p.push(Event.get("a"));
		List<PrefixTreeElement> ptes;
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(1, ptes.size());
		PrefixTreeElement mte;
		mte = ptes.get(0);
		assertEquals(1, mte.size());
		p.push(Event.get("a"));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(1, ptes.size());
		mte = ptes.get(0);
		assertEquals(1, mte.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test2()
	{
		List<PrefixTreeElement> ptes;
		PrefixTreeElement mte;
		Proxy a = new Proxy(new AddB());
		SinkLast sink = new SinkLast();
		Connector.connect(a, sink);
		Pushable p = a.getPushableInput();
		p.push(Event.get("a"));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(1, ptes.size());
		mte = ptes.get(0);
		assertEquals(1, mte.size());
		p.push(Event.get("a"));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(1, ptes.size());
		mte = ptes.get(0);
		assertEquals(2, mte.size());
		p.push(Event.get("b"));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(1, ptes.size());
		mte = ptes.get(0);
		assertEquals(4, mte.size());
		p.push(Event.get("a"));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(1, ptes.size());
		mte = ptes.get(0);
		assertEquals(4, mte.size());
		List<Event> applied = new ArrayList<Event>();
		applied.add(Event.get("a"));
		a.apply(applied);
		p.push(Event.get("a"));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(1, ptes.size());
		mte = ptes.get(0);
		assertEquals(1, mte.size());
	}
	
	public static class AddB extends UniformProcessor
	{
		public AddB()
		{
			super(1, 1);
		}

		@Override
		protected boolean compute(Object[] input, Object[] output)
		{
			List<Event> evts = new ArrayList<Event>();
			evts.add((Event) input[0]);
			if (!evts.contains(Event.get("b")))
			{
				evts.add(Event.get("b"));
			}
			MultiEvent me = new MultiEvent(evts);
			output[0] = me;
			return true;
		}

		@Override
		public Processor duplicate(boolean with_state)
		{
			return new AddB();
		}
		
	}
	
	public static class WrapEvent extends UniformProcessor
	{
		public WrapEvent()
		{
			super(1, 1);
		}

		@Override
		protected boolean compute(Object[] input, Object[] output)
		{
			List<Event> evts = new ArrayList<Event>();
			evts.add((Event) input[0]);
			MultiEvent me = new MultiEvent(evts);
			output[0] = me;
			return true;
		}

		@Override
		public Processor duplicate(boolean with_state)
		{
			return new WrapEvent();
		}
		
	}
}
