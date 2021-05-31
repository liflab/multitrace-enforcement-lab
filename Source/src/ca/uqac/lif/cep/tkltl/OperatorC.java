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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import multitrace.Endpoint;
import multitrace.Quadrilean;

/**
 * The <i>C</i> operator of TK-LTL.
 */
public class OperatorC extends SynchronousProcessor
{
	/**
	 * The formula to evaluate.
	 */
	protected Processor m_phi;
	
	/**
	 * The endpoints for each suffix of the input trace.
	 */
	protected List<Endpoint<Object,Quadrilean.Value>> m_endpoints;
	
	/**
	 * The number of suffixes where phi is not false.
	 */
	protected int m_count;
	
	/**
	 * The value to look for in each endpoint
	 */
	protected Quadrilean.Value m_value;
	
	/**
	 * Creates a new count operator.
	 * @param phi The formula to evaluate
	 */
	public OperatorC(Processor phi, Quadrilean.Value v)
	{
		super(1, 1);
		m_value = v;
		m_count = 0;
		m_phi = phi;
		m_endpoints = new ArrayList<Endpoint<Object,Quadrilean.Value>>();
	}

	@Override
	protected boolean compute(Object[] input, Queue<Object[]> output)
	{
		Endpoint<Object,Quadrilean.Value> ep = new Endpoint<Object,Quadrilean.Value>(m_phi.duplicate());
		m_endpoints.add(ep);
		Iterator<Endpoint<Object,Quadrilean.Value>> it = m_endpoints.iterator();
		int cnt = 0;
		while (it.hasNext())
		{
			Endpoint<Object,Quadrilean.Value> e = it.next();
			Quadrilean.Value v = e.getVerdict(input[0]);
			if (v == m_value)
			{
				cnt++;
			}
			if (v == Quadrilean.Value.TRUE || v == Quadrilean.Value.FALSE)
			{
				it.remove();
			}
		}
		if (m_value != Quadrilean.Value.TRUE && m_value != Quadrilean.Value.FALSE)
		{
			m_count = cnt;
		}
		else
		{
			m_count += cnt;
		}
		output.add(new Object[] {m_count});
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		OperatorC c = new OperatorC(m_phi.duplicate(with_state), m_value);
		if (with_state)
		{
			c.m_count = m_count;
			for (Endpoint<Object,Quadrilean.Value> ep : m_endpoints)
			{
				c.m_endpoints.add(ep.duplicate());
			}
		}
		return c;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_count = 0;
		m_endpoints.clear();
	}
}
