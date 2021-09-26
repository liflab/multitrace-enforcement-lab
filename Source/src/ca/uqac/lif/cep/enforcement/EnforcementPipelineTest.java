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

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.junit.Ignore;
import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.StateMooreMachine.EventTransition;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.tmf.SinkLast;

/**
 * Unit tests for the {@link MonotonicMultiTraceSelector} processor.
 */
public class EnforcementPipelineTest
{
	protected static final Event a = Event.get("a");
	protected static final Event b = Event.get("b");
	protected static final Event c = Event.get("c");
	protected static final Event x = Event.get("x");
	protected static final Event y = Event.get("y");

	@Test
	@Ignore
	public void testHighestString1()
	{
		StateMooreMachine invert_b_c = new StateMooreMachine(1, 1);
		{
			invert_b_c.addTransition(0, new EventTransition(a, 1));
			invert_b_c.addTransition(0, new EventTransition(b, 2));
			invert_b_c.addTransition(0, new EventTransition(c, 3));
			invert_b_c.addTransition(1, new EventTransition(a, 1));
			invert_b_c.addTransition(2, new EventTransition(a, 1));
			invert_b_c.addTransition(3, new EventTransition(a, 1));
			invert_b_c.addTransition(1, new EventTransition(b, 2));
			invert_b_c.addTransition(2, new EventTransition(b, 2));
			invert_b_c.addTransition(3, new EventTransition(b, 2));
			invert_b_c.addTransition(1, new EventTransition(c, 3));
			invert_b_c.addTransition(2, new EventTransition(c, 3));
			invert_b_c.addTransition(3, new EventTransition(c, 3));
			invert_b_c.addSymbol(0, new Constant(new MultiEvent(Event.EPSILON)));
			invert_b_c.addSymbol(1, new Constant(new MultiEvent(a)));
			invert_b_c.addSymbol(2, new Constant(new MultiEvent(a, b)));
			invert_b_c.addSymbol(3, new Constant(new MultiEvent(c)));
		}
		Gate pipeline = new Gate(new NoTwoBs(), new Proxy(invert_b_c), new IntervalFilter(new NoTwoBs(), 1), new IntervalSelector(new HighestString(), 1));
		SinkLast sink = new SinkLast();
		Connector.connect(pipeline, sink);
		Event e = null;
		Pushable p = pipeline.getPushableInput();
		p.push(a);
		e = (Event) sink.getLast()[0];
		assertEquals("a", e.getLabel());
		p.push(b);
		e = (Event) sink.getLast()[0];
		assertEquals("b", e.getLabel());
		p.push(b);
		e = (Event) sink.getLast()[0];
		assertEquals("a", e.getLabel());
	}

	@Test
	public void testAEventuallyB1()
	{
		Gate gate = new Gate(new AFB(), new Proxy(new InsertB()), new IntervalFilter(new AFB(), 3), new IntervalSelector(new TurnInto(1), 3));
		QueueSink sink = new QueueSink();
		Connector.connect(gate, sink);
		Pushable p = gate.getPushableInput();
		Queue<Object> q = sink.getQueue();
		p.push(a);
		assertTrue(q.isEmpty());
		p.push(b);
		assertEquals(2, q.size());
	}
	
	@Test
	public void testAEventuallyB2()
	{
		Gate gate = new Gate(new AFB(), new Proxy(new InsertB()), new IntervalFilter(new AFB(), 5), new IntervalSelector(new Length(), 5));
		QueueSink sink = new QueueSink();
		Connector.connect(gate, sink);
		Pushable p = gate.getPushableInput();
		Queue<Object> q = sink.getQueue();
		p.push(a);
		assertTrue(q.isEmpty());
		p.push(x);
		assertTrue(q.isEmpty());
		p.push(y);
		assertFalse(q.isEmpty());
		assertEquals(a, q.remove());
		assertEquals(x, q.remove());
		assertEquals(b, q.remove());
		assertEquals(y, q.remove());
		p.push(b);
		assertFalse(q.isEmpty());
		assertEquals(b, q.remove());
	}

	public static class InsertB extends SynchronousProcessor
	{
		public InsertB()
		{
			super(1, 1);
		}

		@Override
		protected boolean compute(Object[] input, Queue<Object[]> output)
		{
			MultiTraceElement mte = new MultiTraceElement();
			{
				List<Event> evts = new ArrayList<Event>();
				evts.add(Event.EPSILON);
				evts.add(Event.get("b"));
				MultiEvent me = new MultiEvent(evts);
				mte.add(me);
			}
			{
				List<Event> evts = new ArrayList<Event>();
				evts.add((Event) input[0]);
				MultiEvent me = new MultiEvent(evts);
				mte.add(me);
			}
			output.add(new Object[] {mte});
			return true;
		}

		@Override
		public InsertB duplicate(boolean with_state)
		{
			return new InsertB();
		}

	}
	
	public static class Length extends UniformProcessor
	{
		protected int m_length;
		
		public Length()
		{
			super(1, 1);
			m_length = 0;
		}

		@Override
		protected boolean compute(Object[] input, Object[] output)
		{
			m_length++;
			output[0] = -m_length;
			return true;
		}

		@Override
		public Length duplicate(boolean arg0) 
		{
			Length l = new Length();
			l.m_length = m_length;
			return l;
		}
	}

	public static class AFB extends UniformProcessor
	{

		protected static final transient Event A = Event.get("a");

		protected static final transient Event B = Event.get("b");

		protected boolean m_critical;

		public AFB()
		{
			super(1, 1);
			m_critical = false;
		}

		@Override
		protected boolean compute(Object[] input, Object[] output)
		{
			Event e = (Event) input[0];
			if (e == A)
			{
				m_critical = true;
			}
			if (e == B)
			{
				m_critical = false;
			}
			if (m_critical)
			{
				output[0] = Quadrilean.Value.P_FALSE;
			}
			else
			{
				output[0] = Quadrilean.Value.P_TRUE;
			}
			return true;
		}

		@Override
		public AFB duplicate(boolean with_state)
		{
			AFB a = new AFB();
			if (with_state)
			{
				a.m_critical = m_critical;
			}
			return a;
		}
	}

}
