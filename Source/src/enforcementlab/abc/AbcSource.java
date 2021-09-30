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
package enforcementlab.abc;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.util.ElementPicker;
import enforcementlab.PickerSource;

/**
 * A source of abstract events labeled "a", "b" and "c", occurring with
 * equal probability.
 */
public class AbcSource extends PickerSource<Event>
{
	/**
	 * The name of this event source.
	 */
	public static final transient String NAME = "a-b-c";
	
	public AbcSource(Picker<Float> float_source, int length)
	{
		super(getPicker(float_source), length);
	}
	
	public static List<Event> getAlphabet()
	{
		List<Event> alphabet = new ArrayList<Event>(3);
		alphabet.add(Event.get("a"));
		alphabet.add(Event.get("b"));
		alphabet.add(Event.get("c"));
		return alphabet;
	}
	
	protected static Picker<Event> getPicker(Picker<Float> float_source)
	{
		ElementPicker<Event> picker = new ElementPicker<Event>(float_source);
		picker.add(Event.get("a"), 0.33);
		picker.add(Event.get("b"), 0.33);
		picker.add(Event.get("c"), 0.34);
		return picker;
	}
}
