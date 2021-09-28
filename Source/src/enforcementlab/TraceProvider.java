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

import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.synthia.Picker;
import enforcementlab.abc.AbcSource;
import enforcementlab.casino.CasinoSource;

import static enforcementlab.MultiTraceSelectorExperiment.EVENT_SOURCE;

import java.util.List;

public class TraceProvider
{
	/**
	 * The "maximize gains" scoring formula in the casino scenario.
	 */
	public static final transient String SE_CASINO_RANDOM = "Casino random";
	
	/**
	 * The "maximize gains" scoring formula in the casino scenario.
	 */
	public static final transient String SE_ABC = "a-b-c";
	
	/**
	 * The "maximize gains" scoring formula in the casino scenario.
	 */
	public static final transient String SE_FILE = "File operations";
	
	/**
	 * A Boolean picker.
	 */
	protected Picker<Boolean> m_coin;

	/**
	 * A picker used to feed other random pickers within the class.
	 */
	protected Picker<Float> m_randomFloat;
	
	/**
	 * The length of the traces to generate
	 */
	protected int m_traceLength = 20;
	
	public TraceProvider(Picker<Boolean> coin, Picker<Float> random_float)
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
		switch (r.getString(EVENT_SOURCE))
		{
		case SE_ABC:
			return new AbcSource(m_randomFloat, m_traceLength);
		case SE_CASINO_RANDOM:
			return new CasinoSource(m_coin, m_randomFloat, m_traceLength);
		}		
		return null;
	}
	
	/**
	 * Gets the alphabet (set of events) produced by a source.
	 * @param r The region containing parameters to select the source.
	 * @return The list of possible events
	 */
	public List<Event> getAlphabet(Region r)
	{
		switch (r.getString(EVENT_SOURCE))
		{
		case SE_ABC:
			return AbcSource.getAlphabet();
		case SE_CASINO_RANDOM:
			return CasinoSource.getAlphabet();
		}
		return null;
	}
}
