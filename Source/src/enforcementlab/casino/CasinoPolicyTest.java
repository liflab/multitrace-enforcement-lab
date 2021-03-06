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
	public void testPush1()
	{
		CasinoPolicy fl = new CasinoPolicy(5);
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
		CasinoPolicy fl_dup = fl.duplicate(true);
		SinkLast sink_dup = new SinkLast();
		Connector.connect(fl_dup, sink_dup);
		Pushable p_dup = fl_dup.getPushableInput();
		p_dup.push(new CasinoEvent.Bet("a"));
		assertEquals(Value.FALSE, sink_dup.getLast()[0]);
	}
}
