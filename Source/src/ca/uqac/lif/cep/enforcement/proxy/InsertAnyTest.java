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
package ca.uqac.lif.cep.enforcement.proxy;

import static org.junit.Assert.*;

import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.cep.enforcement.MultiTraceElement;
import ca.uqac.lif.cep.tmf.QueueSink;

public class InsertAnyTest
{
	public static final Event A = Event.get("a");
	public static final Event B = Event.get("b");
	
	@Test
	public void test1()
	{
		MultiTraceElement mte;
		MultiEvent me;
		InsertAny proxy = new InsertAny(1, A);
		QueueSink sink = new QueueSink();
		Connector.connect(proxy, sink);
		Pushable p = proxy.getPushableInput();
		Queue<?> queue = sink.getQueue();
		p.push(A);
		mte = (MultiTraceElement) queue.remove();
		assertEquals(2, mte.size());
		me = mte.get(0);
		assertEquals(2, me.size()); // [a], epsilon
		me = mte.get(1);
		assertEquals(1, me.size()); // [a]
	}
}
