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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.synthia.Picker;

/**
 * A picker that generates random multi-events from a predetermined event
 * alphabet.
 */
public class MultiEventPicker implements Picker<MultiEvent>
{
	/**
	 * A coin used to decide the presence of each uni-event in the multi-event
	 */
	protected Picker<Boolean> m_coin;
	
	/**
	 * The alphabet of events
	 */
	protected Event[] m_alphabet;
	
	/**
	 * Creates a new multi-event picker.
	 * @param coin A coin used to decide the presence of each uni-event
	 * in the multi-event
	 * @param alphabet The alphabet of events
	 */
	public MultiEventPicker(Picker<Boolean> coin, String ... alphabet)
	{
		super();
		m_coin = coin;
		m_alphabet = new Event[alphabet.length];
		for (int i = 0; i < alphabet.length; i++)
		{
			m_alphabet[i] = Event.get(alphabet[i]);
		}
	}
	
	/**
	 * Creates a new multi-event picker.
	 * @param coin A coin used to decide the presence of each uni-event
	 * in the multi-event
	 * @param alphabet The alphabet of events
	 */
	public MultiEventPicker(Picker<Boolean> coin, Event ... alphabet)
	{
		super();
		m_coin = coin;
		m_alphabet = alphabet;
	}
	
	@Override
	public MultiEventPicker duplicate(boolean with_state)
	{
		return new MultiEventPicker(m_coin.duplicate(with_state), m_alphabet);
	}

	@Override
	public MultiEvent pick()
	{
		List<Event> evts = new ArrayList<Event>();
		for (int i = 0; i < m_alphabet.length; i++)
		{
			if (m_coin.pick())
			{
				evts.add(m_alphabet[i]);
			}
		}
		MultiEvent me = new MultiEvent(evts);
		return me;
	}

	@Override
	public void reset()
	{
		m_coin.reset();
	}
}
