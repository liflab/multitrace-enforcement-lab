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

import static enforcementlab.MultiTraceSelectorExperiment.EVENT_SOURCE;

import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.synthia.Picker;
import enforcementlab.casino.CasinoMultiEventPicker;

public class MultiTraceProvider
{
	/**
	 * The "maximize gains" scoring formula in the casino scenario.
	 */
	public static final transient String SE_CASINO_RANDOM = "Casino random";
	
	/**
	 * A Boolean picker.
	 */
	protected Picker<Boolean> m_coin;

	/**
	 * A picker used to feed other random pickers within the class.
	 */
	protected Picker<Float> m_randomFloat;
	
	public MultiTraceProvider(Picker<Boolean> coin, Picker<Float> random_float)
	{
		super();
		m_coin = coin;
		m_randomFloat = random_float;
	}
	
	/**
	 * Gets a multi-event source.
	 * @param r The region containing parameters to select the source.
	 * @return The source, or <tt>null</tt> if no processor could be
	 * produced for the given region.
	 */
	public Source get(Region r)
	{
		String name = r.getString(EVENT_SOURCE);
		if (name.compareTo(SE_CASINO_RANDOM) == 0)
		{
			CasinoMultiEventPicker picker = new CasinoMultiEventPicker(m_coin, m_randomFloat, "a", "b", "c", "d");
			PickerSource<MultiEvent> ps = new PickerSource<MultiEvent>(picker, 100);
			return ps;
		}
		return null;
	}
}
