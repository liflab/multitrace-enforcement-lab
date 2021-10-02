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

import static enforcementlab.museum.MuseumSource.ADULT_IN;
import static enforcementlab.museum.MuseumSource.ADULT_OUT;
import static enforcementlab.museum.MuseumSource.CHILD_IN;
import static enforcementlab.museum.MuseumSource.CHILD_OUT;
import static enforcementlab.museum.MuseumSource.GUARD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_OUT;

public class MuseumScoreTest
{
	@Test
	public void testMinimizeIdleGuards1()
	{
		MinimizeIdleGuards sf = new MinimizeIdleGuards();
		SinkLast sink = new SinkLast();
		Connector.connect(sf, sink);
		Pushable p = sf.getPushableInput();
		p.push(ADULT_IN);
		assertEquals(0, sink.getLast()[0]);
		p.push(GUARD_IN);
		assertEquals(0, sink.getLast()[0]);
		p.push(ADULT_IN);
		assertEquals(-1, sink.getLast()[0]);
		p.push(CHILD_IN);
		assertEquals(-1, sink.getLast()[0]);
		p.push(ADULT_IN);
		assertEquals(-1, sink.getLast()[0]);
		p.push(GUARD_IN);
		assertEquals(-1, sink.getLast()[0]);
		p.push(ADULT_IN);
		assertEquals(-2, sink.getLast()[0]);
		p.push(ADULT_IN);
		assertEquals(-3, sink.getLast()[0]);
		p.push(CHILD_IN);
		assertEquals(-3, sink.getLast()[0]);
		p.push(CHILD_OUT);
		assertEquals(-3, sink.getLast()[0]);
		p.push(ADULT_OUT);
		assertEquals(-4, sink.getLast()[0]);
		p.push(GUARD_OUT);
		assertEquals(-4, sink.getLast()[0]);
		p.push(GUARD_OUT);
		assertEquals(-4, sink.getLast()[0]);
		p.push(ADULT_IN);
		assertEquals(-4, sink.getLast()[0]);
	}
}
