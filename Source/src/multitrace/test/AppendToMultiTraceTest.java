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

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.SinkLast;
import multitrace.AppendToMultiTrace;
import multitrace.MultiEvent;
import multitrace.MultiTraceElement;

/**
 * Unit tests for {@link AppendToMultiTrace}.
 */
public class AppendToMultiTraceTest
{
	@Test
	public void test1()
	{
		AppendToMultiTrace a = new AppendToMultiTrace();
		SinkLast sink = new SinkLast();
		Connector.connect(a, sink);
		Pushable p = a.getPushableInput();
		p.push(new MultiEvent("a"));
		MultiTraceElement mte;
		mte = (MultiTraceElement) sink.getLast()[0];
		assertEquals(1, mte.size());
		p.push(new MultiEvent("a"));
		mte = (MultiTraceElement) sink.getLast()[0];
		assertEquals(1, mte.size());
		p.push(new MultiEvent("a", "b", "c"));
		mte = (MultiTraceElement) sink.getLast()[0];
		assertEquals(1, mte.size());
		p.push(new MultiEvent("a"));
		mte = (MultiTraceElement) sink.getLast()[0];
		assertEquals(3, mte.size());
	}
}
