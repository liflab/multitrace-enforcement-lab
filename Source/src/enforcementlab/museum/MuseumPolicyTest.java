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
package enforcementlab.museum;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.SinkLast;
import ca.uqac.lif.cep.enforcement.Quadrilean.Value;

import static enforcementlab.museum.MuseumSource.ADULT_IN;
import static enforcementlab.museum.MuseumSource.ADULT_OUT;
import static enforcementlab.museum.MuseumSource.CHILD_IN;
import static enforcementlab.museum.MuseumSource.CHILD_OUT;
import static enforcementlab.museum.MuseumSource.GUARD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_OUT;

public class MuseumPolicyTest
{
	@Test
	public void test1()
	{
		MuseumPolicy mp = new MuseumPolicy();
		SinkLast sink = new SinkLast();
		Connector.connect(mp, sink);
		Pushable p = mp.getPushableInput();
		p.push(ADULT_IN);
		assertEquals(Value.P_TRUE, sink.getLast()[0]);
		p.push(ADULT_OUT);
		assertEquals(Value.P_TRUE, sink.getLast()[0]);
		p.push(GUARD_IN);
		assertEquals(Value.P_TRUE, sink.getLast()[0]);
		p.push(ADULT_OUT);
		assertEquals(Value.FALSE, sink.getLast()[0]);
	}
	
	@Test
	public void test2()
	{
		MuseumPolicy mp = new MuseumPolicy();
		SinkLast sink = new SinkLast();
		Connector.connect(mp, sink);
		Pushable p = mp.getPushableInput();
		p.push(ADULT_IN);
		assertEquals(Value.P_TRUE, sink.getLast()[0]);
		p.push(CHILD_IN);
		assertEquals(Value.FALSE, sink.getLast()[0]);
	}
	
	@Test
	public void test3()
	{
		MuseumPolicy mp = new MuseumPolicy();
		SinkLast sink = new SinkLast();
		Connector.connect(mp, sink);
		Pushable p = mp.getPushableInput();
		p.push(ADULT_IN);
		assertEquals(Value.P_TRUE, sink.getLast()[0]);
		p.push(GUARD_IN);
		assertEquals(Value.P_TRUE, sink.getLast()[0]);
		p.push(CHILD_IN);
		assertEquals(Value.P_TRUE, sink.getLast()[0]);
		p.push(GUARD_OUT);
		assertEquals(Value.FALSE, sink.getLast()[0]);
		p.push(GUARD_IN);
		assertEquals(Value.FALSE, sink.getLast()[0]);
	}
}
