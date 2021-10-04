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

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.Checkpointable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.cep.enforcement.MultiTraceElement;
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
	protected boolean[] m_inGame;

	protected static final transient StartGame START = new StartGame("A");

	protected static final transient Event END = Event.getAdded(new EndGame("A").getLabel());

	/**
	 * Creates a new instance of the proxy.
	 */
	public CasinoProxy()
	{
		super(1, 1);
		m_inGame = new boolean[] {false, false};
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event e = (Event) inputs[0];
		String label = e.getLabel();
		MultiTraceElement mte = new MultiTraceElement();
		if (m_inGame[0])
		{
			MultiEvent ins_before = new MultiEvent(END, Event.EPSILON); // May end the game
			mte.add(ins_before);
		}
		if (label.startsWith("Start"))
		{
			m_inGame[0] = true;
		}
		if (label.startsWith("End"))
		{
			m_inGame[0] = false;
		}
		if (label.startsWith("Bet") || label.startsWith("Pay(casino"))
		{
			// A bet may or may not be accepted, and the casino may refuse to pay
			MultiEvent me = new MultiEvent(e, Event.getDeleted(label));
			mte.add(me);
		}
		else 
		{
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
