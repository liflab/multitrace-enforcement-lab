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
package enforcementlab;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.util.ElementPicker;

public class TraceProviderTest
{
	@Test
	public void test()
	{
		RandomFloat rf = new RandomFloat();
		rf.setSeed(0);
		ElementPicker<Event> picker = new ElementPicker<Event>(rf);
		picker.add(Event.get("a"), 0.33);
		picker.add(Event.get("b"), 0.33);
		picker.add(Event.get("c"), 0.34);
		PickerSource<Event> ps = new PickerSource<Event>(picker, 100);
		Pullable p = ps.getPullableOutput();
		List<Event> list = new ArrayList<Event>();
		for (int i = 0; i < 20; i++)
		{
			list.add((Event) p.pull());
		}
		ps.reset();
		for (int i = 0; i < 20; i++)
		{
			assertEquals(list.get(i), (Event) p.pull());
		}
	}
}
