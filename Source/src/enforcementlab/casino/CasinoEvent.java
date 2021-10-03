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

import ca.uqac.lif.cep.enforcement.Event;

/**
 * Generic class for all events of the casino example.
 */
public class CasinoEvent extends Event
{
	/**
	 * Creates a new casino even with given label.
	 * @param s The label
	 */
	protected CasinoEvent(String s)
	{
		super(s);
	}
	
	/**
	 * Event indicating a bet made by a player.
	 */
	public static class Bet extends CasinoEvent
	{
		/**
		 * Creates a new bet event.
		 * @param player The player placing the bet
		 */
		public Bet(String player)
		{
			super("Bet(" + player + ")");
		}	
		
		public String getPlayer()
		{
			return m_label.substring(4, m_label.length() - 5);
		}
	}
	
	public static class StartGame extends CasinoEvent
	{
		public StartGame(String player)
		{
			super("StartGame(" + player + ")");
		}	
		
		public String getPlayer()
		{
			return m_label.substring(10, m_label.length() - 11);
		}
	}
	
	public static class EndGame extends CasinoEvent
	{
		public EndGame(String player)
		{
			super("EndGame(" + player + ")");
		}
		
		public String getPlayer()
		{
			return m_label.substring(8, m_label.length() - 9);
		}
	}
	
	public static class Pay extends CasinoEvent
	{
		public Pay(String from, String to)
		{
			super("Pay(" + from + "," + to + ")");
		}
		
		public String getFrom()
		{
			return m_label.split(",")[0].substring(4); 
		}
		
		public String getTo()
		{
			String part = m_label.split(",")[1];
			return part.substring(0, part.length() - 1); 
		}
	}
	
	
}
