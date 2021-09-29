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
package enforcementlab.file;

import static org.junit.Assert.*;

import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.Quadrilean;
import ca.uqac.lif.cep.enforcement.Quadrilean.Value;
import ca.uqac.lif.cep.enforcement.StateMooreMachine;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.tmf.QueueSink;

public class FileLifecycleTest
{
	@Test
	public void test1()
	{
		Quadrilean.Value v; 
		MooreMachine fl = new FileLifecycle();
		QueueSink sink = new QueueSink();
		Connector.connect(fl, sink);
		Queue<Object> queue = sink.getQueue();
		Pushable p = fl.getPushableInput();
		p.push(Event.get("Open 1"));
		v = (Quadrilean.Value) queue.remove();
		assertEquals(Value.P_TRUE, v);
		p.push(Event.get("Close 1"));
		v = (Quadrilean.Value) queue.remove();
		assertEquals(Value.P_TRUE, v);
		p.push(Event.get("Read 1"));
		v = (Quadrilean.Value) queue.remove();
		assertEquals(Value.FALSE, v);
		p.push(Event.get("Open 1"));
		v = (Quadrilean.Value) queue.remove();
		assertEquals(Value.FALSE, v);
	}
	
	@Test
	public void test2()
	{
		Quadrilean.Value v; 
		MooreMachine fl = new FileLifecycle();
		QueueSink sink = new QueueSink();
		Connector.connect(fl, sink);
		Queue<Object> queue = sink.getQueue();
		Pushable p = fl.getPushableInput();
		p.push(Event.get("Open 1"));
		v = (Quadrilean.Value) queue.remove();
		assertEquals(Value.P_TRUE, v);
		p.push(Event.get("Read 1"));
		v = (Quadrilean.Value) queue.remove();
		assertEquals(Value.P_TRUE, v);
		StateMooreMachine fl2 = (StateMooreMachine) fl.duplicate(true);
		QueueSink sink2 = new QueueSink();
		Connector.connect(fl2, sink2);
		Queue<Object> queue2 = sink2.getQueue();
		Pushable p2 = fl2.getPushableInput();
		p2.push(Event.get("Read 1"));
		v = (Quadrilean.Value) queue2.remove();
		assertEquals(Value.P_TRUE, v);
		p2.push(Event.get("Open 1"));
		v = (Quadrilean.Value) queue2.remove();
		assertEquals(Value.FALSE, v);
	}
}
