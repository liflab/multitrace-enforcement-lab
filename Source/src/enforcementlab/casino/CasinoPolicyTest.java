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
package enforcementlab.casino;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.enforcement.Quadrilean.Value;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.SinkLast;

public class CasinoPolicyTest
{
	@Test
	public void testPull1()
	{
		QueueSource source = new QueueSource().setEvents(
				new CasinoEvent.Bet("a"),
				new CasinoEvent.Pay("casino", "."),
				new CasinoEvent.Pay("casino", "."),
				new CasinoEvent.Pay("casino", "."),
				new CasinoEvent.Bet("a"),
				new CasinoEvent.Pay(".", "casino"),
				new CasinoEvent.Pay(".", "casino"));
		CasinoPolicy fl = new CasinoPolicy(true);
		Connector.connect(source, fl);
		Pullable p = fl.getPullableOutput();
		Pullable t1 = fl.getPullableOutput(1);
		Pullable t2 = fl.getPullableOutput(2);
		System.out.println(t1.pull() + " " + t2.pull());
		assertEquals(Value.TRUE, p.pull());
		System.out.println(t1.pull() + " " + t2.pull());
		assertEquals(Value.TRUE, p.pull());
		System.out.println(t1.pull() + " " + t2.pull());
		assertEquals(Value.TRUE, p.pull());
		System.out.println(t1.pull() + " " + t2.pull());
		assertEquals(Value.TRUE, p.pull());
		System.out.println(t1.pull() + " " + t2.pull());
		assertEquals(Value.FALSE, p.pull());
		System.out.println(t1.pull() + " " + t2.pull());
		assertEquals(Value.FALSE, p.pull());
		System.out.println(t1.pull() + " " + t2.pull());
		assertEquals(Value.FALSE, p.pull());
		System.out.println(t1.pull() + " " + t2.pull());
	}
	
	@Test
	public void testPush1()
	{
		ProcessorPullWrapper fl = new ProcessorPullWrapper(new CasinoPolicy(false));
		SinkLast sink = new SinkLast();
		Connector.connect(fl, sink);
		Pushable p = fl.getPushableInput();
		p.push(new CasinoEvent.Bet("a"));
		assertEquals(Value.TRUE, sink.getLast()[0]);
		p.push(new CasinoEvent.Pay("casino", "."));
		assertEquals(Value.TRUE, sink.getLast()[0]);
		p.push(new CasinoEvent.Pay("casino", "."));
		assertEquals(Value.TRUE, sink.getLast()[0]);
		p.push(new CasinoEvent.Pay("casino", "."));
		assertEquals(Value.TRUE, sink.getLast()[0]);
		p.push(new CasinoEvent.Bet("a"));
		assertEquals(Value.FALSE, sink.getLast()[0]);
		p.push(new CasinoEvent.Pay(".", "casino"));
		assertEquals(Value.FALSE, sink.getLast()[0]);
		p.push(new CasinoEvent.Pay(".", "casino"));
		assertEquals(Value.FALSE, sink.getLast()[0]);
	}
}
