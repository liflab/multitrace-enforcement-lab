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
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.ltl.Troolean;
import multitrace.Endpoint;

/**
 * The <i>D</i> operator of TK-LTL.
 * @author sylvain
 *
 */
public class OperatorD extends SynchronousProcessor
{	
	/**
	 * The number of events submitted to &psi; since &phi; was observed to hold.
	 */
	protected int m_phiIndex;
	
	/**
	 * The number of events submitted to &psi; since &phi; was observed to hold.
	 */
	protected int m_psiIndex;
	
	/**
	 * The left-hand side condition of the operator.
	 */
	protected Processor m_phi;
	
	/**
	 * The right-hand side condition of the operator.
	 */
	protected Processor m_psi;
	
	/**
	 * The endpoints for the left-hand side condition of the operator.
	 */
	protected List<Endpoint<Object,Troolean.Value>> m_phiEndpoints;
	
	/**
	 * The endpoints for the right-hand side condition of the operator.
	 */
	protected List<Endpoint<Object,Troolean.Value>> m_psiEndpoints;
	
	/**
	 * Creates a new instance of the processor.
	 * @param phi The left-hand side condition of the operator
	 * @param psi The right-hand side condition of the operator
	 */
	public OperatorD(Processor phi, Processor psi)
	{
		super(1, 1);
		m_phi = phi;
		m_psi = psi;
		m_phiEndpoints = new ArrayList<Endpoint<Object,Troolean.Value>>();
		m_psiEndpoints = new ArrayList<Endpoint<Object,Troolean.Value>>();
		m_phiIndex = -1;
		m_psiIndex = -1;
	}

	@Override
	protected boolean compute(Object[] input, Queue<Object[]> output)
	{
		if (m_psiIndex >= 0)
		{
			output.add(new Object[] {m_psiIndex});
			return true;
		}
		if (m_phiIndex < 0)
		{
			m_phiEndpoints.add(new Endpoint<Object,Troolean.Value>(m_phi.duplicate()));
			for (int i = 0; i < m_phiEndpoints.size(); i++)
			{
				Endpoint<Object,Troolean.Value> ep = m_phiEndpoints.get(i);
				Troolean.Value v = ep.getVerdict(input[0]);
				if (v == Troolean.Value.TRUE)
				{
					// We found the first index where phi holds
					m_phiIndex = m_phiEndpoints.size() - i;
					m_phiEndpoints.clear();
					break;
				}
			}
		}
		if (m_phiIndex >= 0 && m_psiIndex < 0)
		{
			m_psiEndpoints.add(new Endpoint<Object,Troolean.Value>(m_psi.duplicate()));
			for (int i = 0; i < m_phiEndpoints.size(); i++)
			{
				Endpoint<Object,Troolean.Value> ep = m_phiEndpoints.get(i);
				Troolean.Value v = ep.getVerdict(input[0]);
				if (v == Troolean.Value.TRUE)
				{
					// We found the first index where psi holds
					m_psiIndex = m_phiIndex + i;
					m_psiEndpoints.clear();
					break;
				}
			}
		}
		output.add(new Object[] {Math.max(0, m_psiIndex)});
		return true;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_phiEndpoints.clear();
		m_psiEndpoints.clear();
		m_phiIndex = -1;
		m_psiIndex = -1;
	}

	@Override
	public OperatorD duplicate(boolean with_state)
	{
		OperatorD d = new OperatorD(m_phi.duplicate(), m_psi.duplicate());
		if (with_state)
		{
			d.m_phiIndex = m_phiIndex;
			d.m_psiIndex = m_psiIndex;
			for (Endpoint<Object,Troolean.Value> ep : m_phiEndpoints)
			{
				d.m_phiEndpoints.add(ep.duplicate());
			}
			for (Endpoint<Object,Troolean.Value> ep : m_psiEndpoints)
			{
				d.m_psiEndpoints.add(ep.duplicate());
			}
		}
		return d;
	}
}
