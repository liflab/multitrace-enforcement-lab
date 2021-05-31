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

import ca.uqac.lif.cep.SynchronousProcessor;
import multitrace.Quadrilean.Value;
import multitrace.Endpoint;

/**
 * Base class for all the quantifiers of TK-LTL. This class implements the
 * <i>P</i><sub>~<i>k</i></sub> operator, and its descendants override this
 * behavior to produce the existential and universal quantifier.
 */
public class OperatorP extends SynchronousProcessor
{
	/**
	 * The counter.
	 */
	protected Endpoint<Object,Integer> m_countEndpoint;
	
	/**
	 * The value to which the counter is compared.
	 */
	protected int m_k;
	
	/**
	 * The comparison being done on the value <i>k</i>.
	 */
	protected Comparison m_comparison;
	
	/**
	 * Equality comparison.
	 */
	public static final transient Equals equals = new Equals();
	
	/**
	 * Greater than comparison.
	 */
	public static final transient GreaterThan greaterThan = new GreaterThan();

	/**
	 * Creates a new instance of the operator.
	 * @param c The counter
	 * @param comp The comparison being done on the value <i>k</i>
	 * @param k The value to which the counter is compared
	 */
	public OperatorP(OperatorC c, Comparison comp, int k)
	{
		super(1, 1);
		m_countEndpoint = new Endpoint<Object,Integer>(c);
		m_k = k;
	}
	
	/**
	 * Creates a new instance of the operator.
	 * @param c The endpoint for the counter
	 * @param comp The comparison being done on the value <i>k</i>
	 * @param k The value to which the counter is compared
	 */
	protected OperatorP(Endpoint<Object,Integer> ep, Comparison comp, int k)
	{
		super(1, 1);
		m_countEndpoint = ep;
		m_k = k;
	}
	
	@Override
	protected boolean compute(Object[] input, Queue<Object[]> output)
	{
		output.add(new Object[] {m_comparison.compare(m_countEndpoint.getLastValue(input[0]), m_k)});
		return true;
	}

	@Override
	public OperatorP duplicate(boolean with_state)
	{
		OperatorP p = new OperatorP(m_countEndpoint.duplicate(), m_comparison, m_k);
		return p;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_countEndpoint.reset();
	}
	
	/**
	 * A comparison between two integers.
	 */
	public interface Comparison
	{
		/**
		 * Compares two integers.
		 * @param x The first integer
		 * @param y The second integer
		 * @return The result of the comparison
		 */
		public Value compare(int x, int y);
	}
	
	/**
	 * Implementation of equality.
	 */
	protected static class Equals implements Comparison
	{
		@Override
		public Value compare(int x, int y)
		{
			if (x == y)
			{
				return Value.TRUE;
			}
			return Value.FALSE;
		}		
	}
	
	/**
	 * Implementation of the "greater than" comparison.
	 */
	protected static class GreaterThan implements Comparison
	{
		@Override
		public Value compare(int x, int y)
		{
			if (x > y)
			{
				return Value.TRUE;
			}
			return Value.FALSE;
		}		
	}
}
