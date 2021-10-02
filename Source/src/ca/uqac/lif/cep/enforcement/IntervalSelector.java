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
package ca.uqac.lif.cep.enforcement;

import ca.uqac.lif.cep.Processor;

public class IntervalSelector extends Selector
{
	protected int m_interval;
	
	public IntervalSelector(Processor rho, int interval)
	{
		super(rho);
		m_interval = interval;
	}

	@Override
	protected boolean decide()
	{
		return m_elements.size() >= m_interval;
	}

	@Override
	public IntervalSelector duplicate(boolean with_state)
	{
		IntervalSelector is = new IntervalSelector(m_rho.duplicate(with_state), m_interval);
		super.copyInto(is, with_state);
		return is;
	}
}
