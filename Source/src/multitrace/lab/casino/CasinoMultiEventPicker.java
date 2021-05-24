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
package multitrace.lab.casino;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.synthia.Picker;
import multitrace.Event;
import multitrace.MultiEvent;

/**
 * A picker producing events from the casino example.
 */
public class CasinoMultiEventPicker implements Picker<MultiEvent>
{
	/**
	 * A coin used to decide the presence of each uni-event in the multi-event.
	 */
	protected Picker<Boolean> m_coin;

	/**
	 * A picker used to feed other random pickers within the class.
	 */
	protected Picker<Float> m_randomFloat;

	/**
	 * The fixed array of player names to pick from. The first element of this
	 * array is expected to be the string "casino".
	 */
	protected String[] m_players;

	/**
	 * Creates a new instance of the picker.
	 * @param coin A coin used to decide the presence of each uni-event in the
	 * multi-event
	 * @param random_float A picker used to feed other random pickers within the
	 * class
	 * @param players The list of player names to pick from. The first element of
	 * this array is expected to be the string "casino".
	 */
	public CasinoMultiEventPicker(Picker<Boolean> coin, Picker<Float> random_float, String ... players)
	{
		super();
		m_coin = coin;
		m_randomFloat = random_float;
		m_players = players;
	}

	@Override
	public CasinoMultiEventPicker duplicate(boolean with_state)
	{
		CasinoMultiEventPicker c = new CasinoMultiEventPicker(m_coin.duplicate(with_state), m_randomFloat.duplicate(with_state), m_players);
		return c;
	}

	@Override
	public MultiEvent pick()
	{
		List<Event> evts = new ArrayList<Event>();
		// Generate a start game?
		if (m_coin.pick())
		{
			String player = pickPlayer(false);
			evts.add(new CasinoEvent.StartGame(player));
		}
		// Generate an end game?
		if (m_coin.pick())
		{
			String player = pickPlayer(false);
			evts.add(new CasinoEvent.EndGame(player));
		}
		// Generate a bet?
		if (m_coin.pick())
		{
			// How many bets?
			int num_bets = (int) (m_randomFloat.pick() * (m_players.length - 1));
			for (int i = 0; i < num_bets; i++)
			{
				String player = pickPlayer(false);
				evts.add(new CasinoEvent.Bet(player));
			}
		}
		// Generate a payment?
		if (m_coin.pick())
		{
			String from = pickPlayer(true);
			String to = pickPlayer(true);
			evts.add(new CasinoEvent.Pay(from, to));
		}
		return new MultiEvent(evts);
	}

	/**
	 * Randomly picks a player name.
	 * @param including_casino Set to <tt>true</tt> to include the casino in the
	 * possible player names
	 * @return The player name.
	 */
	protected String pickPlayer(boolean including_casino)
	{
		int range = including_casino ? m_players.length : m_players.length - 1;
		int index = (int) (m_randomFloat.pick() * range);
		if (including_casino)
		{
			return m_players[index];
		}
		return m_players[index + 1];
	}

	@Override
	public void reset()
	{
		m_coin.reset();
		m_randomFloat.reset();
	}
}
