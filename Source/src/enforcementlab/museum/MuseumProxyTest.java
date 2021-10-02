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

import static org.junit.Assert.*;

import java.util.List;
import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.enforcement.MultiTraceElement;
import ca.uqac.lif.cep.enforcement.PrefixTreeElement;
import ca.uqac.lif.cep.enforcement.Proxy;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.tmf.SinkLast;

import static enforcementlab.museum.MuseumSource.ADULT_IN;
import static enforcementlab.museum.MuseumSource.ADULT_OUT;
import static enforcementlab.museum.MuseumSource.CHILD_IN;
import static enforcementlab.museum.MuseumSource.CHILD_OUT;
import static enforcementlab.museum.MuseumSource.GUARD_IN;
import static enforcementlab.museum.MuseumSource.GUARD_OUT;

public class MuseumProxyTest
{
	@Test
	public void testAlone1()
	{
		MuseumProxy mp = new MuseumProxy();
		SinkLast sink = new SinkLast();
		Connector.connect(mp, sink);
		Pushable p = mp.getPushableInput();
		p.push(CHILD_IN);
		MultiTraceElement mte = (MultiTraceElement) sink.getLast()[0];
		assertEquals(2, mte.size());
		System.out.println(mte);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWithProxy1()
	{
		List<PrefixTreeElement> list;
		PrefixTreeElement pte;
		Proxy mp = new Proxy(new MuseumProxy());
		QueueSink sink = new QueueSink();
		Connector.connect(mp, sink);
		Queue<Object> queue = sink.getQueue();
		Pushable p = mp.getPushableInput();
		p.push(CHILD_IN);
		list = (List<PrefixTreeElement>) queue.remove();
		System.out.println(list);
		//assertEquals(2, mte.size());
		//System.out.println(mte);
	}
}
