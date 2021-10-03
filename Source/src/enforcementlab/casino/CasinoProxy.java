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

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.cep.enforcement.MultiTraceElement;
import enforcementlab.casino.CasinoEvent.Bet;
import enforcementlab.casino.CasinoEvent.EndGame;
import enforcementlab.casino.CasinoEvent.StartGame;

public class CasinoProxy extends UniformProcessor
{
	/**
	 * A name given to this proxy.
	 */
	public static final String NAME = "Casino proxy";
	
	/**
	 * A flag indicating whether a game is currently in progress.
	 */
	protected boolean m_inGame;
	
	/**
	 * Creates a new instance of the proxy.
	 */
	public CasinoProxy()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		CasinoEvent e = (CasinoEvent) inputs[0];
		MultiTraceElement mte = new MultiTraceElement();
		if (!m_inGame && !(e instanceof StartGame))
		{
			// Any event but StartGame is blocked while not in game
			MultiEvent me = new MultiEvent(Event.getDeleted(e.getLabel()));
			mte.add(me);
		}
		else if (e instanceof Bet)
		{
			// A bet may or may not be accepted
			MultiEvent me = new MultiEvent(e, Event.getDeleted(e.getLabel()));
			mte.add(me);
		}
		else
		{
			if (e instanceof StartGame)
			{
				m_inGame = true;
			}
			if (e instanceof EndGame)
			{
				m_inGame = false;
			}
			// Other events are let through
			MultiEvent me = new MultiEvent(e);
			mte.add(me);
		}
		outputs[0] = mte;
		return true;
	}

	@Override
	public CasinoProxy duplicate(boolean with_state)
	{
		CasinoProxy cp = new CasinoProxy();
		if (with_state)
		{
			cp.m_inGame = m_inGame;
		}
		return cp;
	}
}
