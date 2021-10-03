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

import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.enforcement.Quadrilean;
import ca.uqac.lif.cep.tmf.QueueSink;

public class CasinoPolicyTest
{
	@Test
	public void test1()
	{
		Quadrilean.Value v;
		CasinoPolicy fl = new CasinoPolicy();
		QueueSink sink = new QueueSink();
		Connector.connect(fl, sink);
		Queue<Object> queue = sink.getQueue();
		Pushable p = fl.getPushableInput();
		p.push(new CasinoEvent.StartGame("a"));
		//assertFalse(queue.isEmpty());
		//v = (Quadrilean.Value) queue.remove();
		//assertEquals(Quadrilean.Value.TRUE, v);
		p.push(new CasinoEvent.Bet("a"));
		p.push(new CasinoEvent.Bet("b"));
		p.push(new CasinoEvent.Bet("c"));
	}
}
