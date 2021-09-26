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
package ca.uqac.lif.cep.tkltl;

import java.util.Queue;

import ca.uqac.lif.cep.enforcement.Endpoint;
import ca.uqac.lif.cep.enforcement.Quadrilean;

/**
 * The &exist;<sub>~<i>k</i></sub> operator of TK-LTL.
 */
public class OperatorExists extends OperatorP
{
	/**
	 * Whether the comparison has produced the value true so far.
	 */
	protected boolean m_seenTrue;
	
	/**
	 * Creates a new instance of the operator.
	 * @param c The counter
	 * @param comp The comparison being done on the value <i>k</i>
	 * @param k The value to which the counter is compared
	 */
	public OperatorExists(OperatorC c, Comparison comp, int k)
	{
		super(c, comp, k);
		m_seenTrue = false;
	}

	/**
	 * Creates a new instance of the operator.
	 * @param c The endpoint for the counter
	 * @param comp The comparison being done on the value <i>k</i>
	 * @param k The value to which the counter is compared
	 */
	protected OperatorExists(Endpoint<Object,Integer> ep, Comparison comp, int k)
	{
		super(ep, comp, k);
	}

	@Override
	protected boolean compute(Object[] input, Queue<Object[]> output)
	{
		Quadrilean.Value to_output = Quadrilean.Value.P_TRUE;
		if (m_seenTrue)
		{
			to_output = Quadrilean.Value.TRUE;
		}
		else
		{
			Quadrilean.Value v = m_comparison.compare(m_countEndpoint.getLastValue(input[0]), m_k);
			if (v == Quadrilean.Value.TRUE)
			{
				m_seenTrue = true;
				to_output = Quadrilean.Value.TRUE;
			}
		}
		output.add(new Object[] {to_output});
		return true;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_seenTrue = false;
	}

	@Override
	public OperatorExists duplicate(boolean with_state)
	{
		OperatorExists op = new OperatorExists(m_countEndpoint.duplicate(), m_comparison, m_k);
		if (with_state)
		{
			op.m_seenTrue = m_seenTrue;
		}
		return op;
	}
}
