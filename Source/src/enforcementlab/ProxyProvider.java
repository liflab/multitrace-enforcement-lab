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
package enforcementlab;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.enforcement.Proxy;
import ca.uqac.lif.cep.enforcement.proxy.DeleteAny;
import ca.uqac.lif.cep.enforcement.proxy.InsertAny;
import ca.uqac.lif.labpal.Region;
import enforcementlab.abc.DeleteAnyA;
import enforcementlab.abc.InsertAnyA;
import enforcementlab.abc.Property3;
import enforcementlab.casino.CasinoProxy;
import enforcementlab.museum.MuseumProxy;

public class ProxyProvider
{
	protected TraceProvider m_traceProvider;
	
	public ProxyProvider(TraceProvider tp)
	{
		super();
		m_traceProvider = tp;
	}
	
	public Proxy get(Region r)
	{
		String name = r.getString(GateExperiment.PROXY);
		Processor p = null;
		if (name == null)
		{
			return null;
		}
		switch (name)
		{
		case InsertAny.NAME:
			String policy = r.getString(GateExperiment.POLICY);
			if (policy.compareTo(Property3.NAME) == 0)
			{
				p = new InsertAny(2, m_traceProvider.getAlphabet(r));
			}
			else
			{
				p = new InsertAny(1, m_traceProvider.getAlphabet(r));
			}
			break;
		case InsertAnyA.NAME:
			p = new InsertAnyA();
			break;
		case DeleteAny.NAME:
			p = new DeleteAny(m_traceProvider.getAlphabet(r));
			break;
		case DeleteAnyA.NAME:
			p = new DeleteAnyA();
			break;
		case MuseumProxy.NAME:
			p = new MuseumProxy();
			break;
		case CasinoProxy.NAME:
			p = new CasinoProxy();
			break;
		}
		if (p == null)
		{
			return null;
		}
		return new Proxy(p);
	}
}
