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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.SinkLast;
import multitrace.Event;
import multitrace.IntervalFilter;
import multitrace.MultiEvent;
import multitrace.PrefixTreeElement;

/**
 * Unit tests for the {@link MonotonicMultiTraceSelector} processor.
 */
public class FilterTest
{
	protected static final Event a = Event.get("a");
	protected static final Event b = Event.get("b");
	protected static final MultiEvent A = new MultiEvent(a);
	protected static final MultiEvent B = new MultiEvent(b);
	protected static final MultiEvent AB = new MultiEvent(a, b);
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNoTwoB1()
	{
		List<PrefixTreeElement> ptes;
		IntervalFilter mmon = new IntervalFilter(new NoTwoBs(), 1);
		SinkLast sink = new SinkLast();
		Connector.connect(mmon, sink);
		MultiEvent me = null;
		PrefixTreeElement mte = null;
		Pushable p = mmon.getPushableInput();
		p.push(getList(new PrefixTreeElement(AB)));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(1, ptes.size());
		mte = ptes.get(0);
		assertEquals(1, mte.size());
		me = mte.get(0);
		assertEquals(2, me.size());
		List<Event> applied = new ArrayList<Event>();
		applied.add(b);
		mmon.apply(applied);
		p.push(getList(new PrefixTreeElement(AB)));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(1, ptes.size());
		mte = ptes.get(0);
		assertEquals(1, mte.size());
		me = mte.get(0); // [a,#]
		assertEquals(2, me.size());
		assertTrue(me.contains(a));
		assertTrue(me.contains(Event.DIAMOND));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNoTwoB2()
	{
		List<PrefixTreeElement> ptes;
		IntervalFilter mmon = new IntervalFilter(new NoTwoBs(), 2);
		SinkLast sink = new SinkLast();
		Connector.connect(mmon, sink);
		MultiEvent me = null;
		PrefixTreeElement mte = null;
		Pushable p = mmon.getPushableInput();
		p.push(getList(new PrefixTreeElement(AB)));
		assertNull(sink.getLast()); // Nothing pushed yet
		p.push(getList(new PrefixTreeElement(AB, AB)));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(2, ptes.size());
		mte = ptes.get(0);
		assertEquals(1, mte.size());
		me = mte.get(0); // [a,b]
		assertEquals(2, me.size());
		assertTrue(me.contains(a));
		assertTrue(me.contains(b));
		mte = ptes.get(1);
		assertEquals(2, mte.size());
		me = mte.get(0); // [a,b]
		assertEquals(2, me.size());
		assertTrue(me.contains(a));
		assertTrue(me.contains(b));
		me = mte.get(1); // [a,#]
		assertEquals(2, me.size());
		assertTrue(me.contains(a));
		assertTrue(me.contains(Event.DIAMOND));
		List<Event> applied = new ArrayList<Event>();
		applied.add(a);
		applied.add(b);
		mmon.apply(applied);
		sink.reset();
		p.push(getList(new PrefixTreeElement(AB)));
		assertNull(sink.getLast()); // Nothing pushed yet
		p.push(getList(new PrefixTreeElement(AB, AB)));
		ptes = (List<PrefixTreeElement>) sink.getLast()[0];
		assertEquals(2, ptes.size());
		mte = ptes.get(0);
		assertEquals(1, mte.size());
		me = mte.get(0); // [a,#]
		assertEquals(2, me.size());
		assertTrue(me.contains(a));
		assertTrue(me.contains(Event.DIAMOND));
		mte = ptes.get(1);
		me = mte.get(0); // [#,#]
		assertEquals(2, me.size());
		assertTrue(me.contains(a));
		assertTrue(me.contains(b));
		me = mte.get(1); // [#,#]
		assertEquals(2, me.size());
		assertFalse(me.contains(a));
		assertFalse(me.contains(b));
		assertTrue(me.contains(Event.DIAMOND));
	}
	
	protected static List<PrefixTreeElement> getList(PrefixTreeElement pte)
	{
		List<PrefixTreeElement> list = new ArrayList<PrefixTreeElement>();
		list.add(pte);
		return list;
	}
}
