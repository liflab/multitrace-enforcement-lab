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
import ca.uqac.lif.cep.enforcement.Quadrilean;

public class CasinoPolicy extends UniformProcessor
{
	/**
	 * The name given to this policy.
	 */
	public static final String NAME = "Casino policy";
	
	protected int m_totalBets;
	
	protected int m_balance;
	
	protected int m_initialBalance;
	
	protected boolean m_violated;
	
	protected boolean m_inGame;
	
	public CasinoPolicy(int initial_balance)
	{
		super(1, 1);
		m_totalBets = 0;
		m_initialBalance = initial_balance;
		m_balance = m_initialBalance;
		m_violated = false;
		m_inGame = false;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_totalBets = 0;
		m_balance = m_initialBalance;
		m_violated = false;
		m_inGame = false;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event e = (Event) inputs[0];
		String label = e.getLabel();
		if (label.startsWith("End"))
		{
			m_totalBets = 0;
			if (!m_inGame)
			{
				m_violated = true;
			}
			m_inGame = false;
		}
		else if (label.startsWith("Start"))
		{
			if (m_inGame)
			{
				m_violated = true;
			}
			m_inGame = true;
		}
		else if (label.startsWith("Bet"))
		{
			if (!m_inGame)
			{
				m_violated = true;
			}
			m_totalBets += 1;
		}
		else if (label.startsWith("Pay(casino"))
		{
			if (m_inGame || m_balance == 0)
			{
				m_violated = true;
			}
			m_balance--;
		}
		else
		{
			m_balance++;
		}
		//System.out.println("Bets " + m_totalBets + ", bal " + m_balance);
		if (m_violated || (m_totalBets * 0.25) > m_balance)
		{
			outputs[0] = Quadrilean.Value.FALSE;
			m_violated = true;
		}
		else
		{
			outputs[0] = Quadrilean.Value.TRUE;
		}
		//System.out.println(m_balance);
		return true;
	}

	@Override
	public CasinoPolicy duplicate(boolean with_state)
	{
		CasinoPolicy cp = new CasinoPolicy(m_initialBalance);
		if (with_state)
		{
			cp.m_totalBets = m_totalBets;
			cp.m_balance = m_balance;
			cp.m_violated = m_violated;
			cp.m_inGame = m_inGame;
		}
		return cp;
	}
}
