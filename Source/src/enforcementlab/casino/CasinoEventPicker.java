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
package enforcementlab.casino;

import java.util.List;

import ca.uqac.lif.cep.enforcement.Checkpointable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.synthia.Picker;

/**
 * A picker producing (uni-)events from the casino example.
 */
public class CasinoEventPicker implements Picker<Event>, Checkpointable
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
	 * A flag used to determine if a game is currently in progress.
	 */
	protected boolean[] m_inGame;

	/**
	 * A counter of the number of bets placed in the current game.
	 */
	protected int[] m_numBets;

	/**
	 * Creates a new instance of the picker.
	 * @param coin A coin used to decide the presence of each uni-event in the
	 * multi-event
	 * @param random_float A picker used to feed other random pickers within the
	 * class
	 * @param players The list of player names to pick from. The first element of
	 * this array is expected to be the string "casino".
	 */
	public CasinoEventPicker(Picker<Boolean> coin, Picker<Float> random_float, String ... players)
	{
		super();
		m_coin = coin;
		m_randomFloat = random_float;
		m_players = players;
		m_inGame = new boolean[] {false, false};
		m_numBets = new int[] {0, 0};
	}

	@Override
	public CasinoEventPicker duplicate(boolean with_state)
	{
		CasinoEventPicker c = new CasinoEventPicker(m_coin.duplicate(with_state), m_randomFloat.duplicate(with_state), m_players);
		return c;
	}

	@Override
	public CasinoEvent pick()
	{
		float event_type = m_randomFloat.pick();
		if (!m_inGame[0])
		{
			if (m_numBets[0] > 0)
			{
				// Start by paying
				m_numBets[0]--;
				float casino_pays = m_randomFloat.pick();
				if (casino_pays < 0.5)
				{
					String to = pickPlayer(true);
					return new CasinoEvent.Pay("casino", to);
				}
				else
				{
					String from = pickPlayer(true);
					return new CasinoEvent.Pay(from, "casino");
				}
			}
			else
			{
				// Generate a start
				String player = pickPlayer(false);
				m_inGame[0] = true;
				m_numBets[0] = 0;
				return new CasinoEvent.StartGame(player);
			}
		}
		else
		{
			if (event_type < 0.33 || m_numBets[0] == m_players.length) // All players placed a bet
			{
				// Generate an end
				String player = pickPlayer(false);
				m_inGame[0] = false;
				return new CasinoEvent.EndGame(player);
			}
			else
			{
				// Generate a bet
				String player = pickPlayer(false);
				m_numBets[0]++;
				return new CasinoEvent.Bet(player);
			}
		}
	}
	
	@Override
	public void apply(List<Event> events)
	{
		for (Event e : events)
		{
			String label = e.getLabel();
			if (label.startsWith("Bet"))
			{
				m_numBets[1]++;
			}
			else if (label.startsWith("Pay"))
			{
				m_numBets[1]--;
			}
			else if (label.startsWith("Start"))
			{
				m_numBets[1] = 0;
				m_inGame[1] = true;
			}
			else if (label.startsWith("End"))
			{
				m_inGame[1] = false;
			}
		}
		m_numBets[0] = m_numBets[1];
		m_inGame[0] = m_inGame[1];
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
		m_inGame[0] = false;
		m_inGame[1] = false;
		m_numBets[0] = 0;
		m_numBets[1] = 0;
	}
}
