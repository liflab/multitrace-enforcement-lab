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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.synthia.Picker;
import enforcementlab.PickerSource;

/**
 * A source of events for the casino example.
 */
public class CasinoSource extends PickerSource<Event>
{
	protected static final String[] s_players = new String[] {"a", "b", "c", "d"};
	
	public CasinoSource(Picker<Boolean> coin, Picker<Float> float_source, int length)
	{
		super(new CasinoEventPicker(coin, float_source, s_players), length);
	}
	
	public static List<Event> getAlphabet()
	{
		List<Event> alphabet = new ArrayList<Event>(3);
		for (String player : s_players)
		{
			alphabet.add(new CasinoEvent.Bet(player));
			alphabet.add(new CasinoEvent.StartGame(player));
			alphabet.add(new CasinoEvent.EndGame(player));
			for (String player2 : s_players)
			{
				alphabet.add(new CasinoEvent.Pay(player, player2));					
			}
		}
		return alphabet;
	}
}
