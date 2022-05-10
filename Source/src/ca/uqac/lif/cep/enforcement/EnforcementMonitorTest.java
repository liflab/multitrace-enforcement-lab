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

import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.tmf.QueueSink;

/**
 * Unit tests for {@link EnforcementMonitor}.
 */
public class EnforcementMonitorTest
{
	public static Event a = Event.get("a");
	public static Event b = Event.get("b");
	public static Event c = Event.get("c");
	
	@Test
	public void test1()
	{
		DummyProxy mon = new DummyProxy();
		EnforcementMonitor em = new EnforcementMonitor(mon);
		Pushable p = em.getPushableInput();
		QueueSink s = new QueueSink();
		Connector.connect(em, s);
		Queue<?> q = s.getQueue();
		p.push(a);
		assertEquals(1, q.size());
		assertEquals(a, q.remove());
		p.push(b);
		assertEquals(2, q.size());
		assertEquals(a, q.remove());
		assertEquals(b, q.remove());
	}
	
	protected static class DummyProxy extends SynchronousProcessor
	{
		public DummyProxy()
		{
			super(1, 1);
		}

		@Override
		protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
		{
			MultiTraceElement mte = new MultiTraceElement();
			Event e = (Event) inputs[0];
			if (e.equals(b))
			{
				mte.add(new MultiEvent(a));
				mte.add(new MultiEvent(b));
			}
			else
			{
				mte.add(new MultiEvent(e));
			}
			outputs.add(new Object[] {mte});
			return true;
		}

		@Override
		public Processor duplicate(boolean with_state)
		{
			return new DummyProxy();
		}
	}
}