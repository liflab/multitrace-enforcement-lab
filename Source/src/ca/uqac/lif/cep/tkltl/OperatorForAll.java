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

import ca.uqac.lif.cep.ltl.Troolean;
import multitrace.Endpoint;

/**
 * The &forall;<sub>~<i>k</i></sub> operator of TK-LTL.
 */
public class OperatorForAll extends OperatorP
{
	/**
	 * Whether the comparison has produced the value false so far.
	 */
	protected boolean m_seenFalse;
	
	/**
	 * Creates a new instance of the operator.
	 * @param c The counter
	 * @param comp The comparison being done on the value <i>k</i>
	 * @param k The value to which the counter is compared
	 */
	public OperatorForAll(OperatorC c, Comparison comp, int k)
	{
		super(c, comp, k);
		m_seenFalse = false;
	}

	/**
	 * Creates a new instance of the operator.
	 * @param c The endpoint for the counter
	 * @param comp The comparison being done on the value <i>k</i>
	 * @param k The value to which the counter is compared
	 */
	protected OperatorForAll(Endpoint<Object,Integer> ep, Comparison comp, int k)
	{
		super(ep, comp, k);
	}

	@Override
	protected boolean compute(Object[] input, Queue<Object[]> output)
	{
		Troolean.Value to_output = Troolean.Value.INCONCLUSIVE;
		if (m_seenFalse)
		{
			to_output = Troolean.Value.FALSE;
		}
		else
		{
			Troolean.Value v = m_comparison.compare(m_countEndpoint.getVerdict(input[0]), m_k);
			if (v == Troolean.Value.FALSE)
			{
				m_seenFalse = true;
				to_output = Troolean.Value.FALSE;
			}
		}
		output.add(new Object[] {to_output});
		return true;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_seenFalse = false;
	}

	@Override
	public OperatorForAll duplicate(boolean with_state)
	{
		OperatorForAll op = new OperatorForAll(m_countEndpoint.duplicate(), m_comparison, m_k);
		if (with_state)
		{
			op.m_seenFalse = m_seenFalse;
		}
		return op;
	}
}
