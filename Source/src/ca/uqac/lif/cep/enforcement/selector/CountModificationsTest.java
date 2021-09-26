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
package ca.uqac.lif.cep.enforcement.selector;

import static org.junit.Assert.assertEquals;

import java.util.Queue;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.tmf.QueueSink;

public class CountModificationsTest
{
	@Test
	public void test1()
	{
		CountModifications cm = new CountModifications();
		QueueSink sink = new QueueSink();
		Connector.connect(cm, sink);
		Queue<?> queue = sink.getQueue();
		Pushable p = cm.getPushableInput();
		int score;
		p.push(Event.get("a"));
		score = (int) queue.remove();
		assertEquals(0, score);
		p.push(Event.getAdded("a"));
		score = (int) queue.remove();
		assertEquals(-1, score);
		p.push(Event.get("b"));
		score = (int) queue.remove();
		assertEquals(-1, score);
		p.push(Event.getDeleted("b"));
		score = (int) queue.remove();
		assertEquals(-2, score);
		cm.reset();
		p.push(Event.getAdded("a"));
		score = (int) queue.remove();
		assertEquals(-1, score);
	}
}
