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
import ca.uqac.lif.cep.enforcement.Quadrilean;
import ca.uqac.lif.cep.enforcement.Quadrilean.Value;
import ca.uqac.lif.cep.functions.UnaryFunction;

public class CasinoFunction
{
	public static final IsBet isBet = new IsBet();
	
	public static final IsStartGame isStart = new IsStartGame();
	
	public static final IsEndGame isEnd = new IsEndGame();
	
	public static final CasinoPays casinoPays = new CasinoPays();
	
	public static final CasinoPaid casinoPaid = new CasinoPaid();

	public static class IsBet extends UnaryFunction<Event,Quadrilean.Value>
	{
		protected IsBet()
		{
			super(Event.class, Quadrilean.Value.class);
		}

		@Override
		public Value getValue(Event e)
		{
			if (e.getLabel().startsWith("Bet"))
			{
				return Quadrilean.Value.TRUE;
			}
			return Quadrilean.Value.FALSE;
		}

		@Override
		public String toString()
		{
			return "Bet(.)";
		}
	}

	public static class IsStartGame extends UnaryFunction<Event,Quadrilean.Value>
	{
		protected IsStartGame()
		{
			super(Event.class, Quadrilean.Value.class);
		}

		@Override
		public Value getValue(Event e)
		{
			if (e.getLabel().startsWith("Start"))
			{
				return Quadrilean.Value.TRUE;
			}
			return Quadrilean.Value.FALSE;
		}

		@Override
		public String toString()
		{
			return "StartGame(.)";
		}
	}

	public static class IsEndGame extends UnaryFunction<Event,Quadrilean.Value>
	{
		protected IsEndGame()
		{
			super(Event.class, Quadrilean.Value.class);
		}

		@Override
		public Value getValue(Event e)
		{
			if (e.getLabel().startsWith("End"))
			{
				return Quadrilean.Value.TRUE;
			}
			return Quadrilean.Value.FALSE;
		}

		@Override
		public String toString()
		{
			return "EndGame(.)";
		}
	}

	public static class CasinoPays extends UnaryFunction<Event,Quadrilean.Value>
	{
		protected CasinoPays()
		{
			super(Event.class, Quadrilean.Value.class);
		}

		@Override
		public Value getValue(Event e)
		{
			if (e.getLabel().contains("casino,"))
			{
				return Quadrilean.Value.TRUE;
			}
			return Quadrilean.Value.FALSE;
		}

		@Override
		public String toString()
		{
			return "Pay(casino,.)";
		}
	}
	
	public static class CasinoPaid extends UnaryFunction<Event,Quadrilean.Value>
	{
		protected CasinoPaid()
		{
			super(Event.class, Quadrilean.Value.class);
		}

		@Override
		public Value getValue(Event e)
		{
			if (e.getLabel().contains(",casino"))
			{
				return Quadrilean.Value.TRUE;
			}
			return Quadrilean.Value.FALSE;
		}

		@Override
		public String toString()
		{
			return "Pay(.,casino)";
		}
	}
}
