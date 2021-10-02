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
import enforcementlab.file.FileSource;
import enforcementlab.museum.MuseumSource;

import static enforcementlab.MultiTraceSelectorExperiment.EVENT_SOURCE;

import java.util.List;

public class TraceProvider
{	
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
	protected int m_traceLength = 100;
	
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
		case AbcSource.NAME:
			return new AbcSource(m_randomFloat, m_traceLength);
		case CasinoSource.NAME:
			return new CasinoSource(m_coin, m_randomFloat, m_traceLength);
		case FileSource.NAME:
			return new FileSource(m_randomFloat, 0.1f, m_traceLength);
		case MuseumSource.NAME:
			return new MuseumSource(m_randomFloat, m_coin, m_traceLength);
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
		case AbcSource.NAME:
			return AbcSource.getAlphabet();
		case CasinoSource.NAME:
			return CasinoSource.getAlphabet();
		case FileSource.NAME:
			return FileSource.getAlphabet();
		case MuseumSource.NAME:
			return MuseumSource.getAlphabet();
		}
		return null;
	}
}
