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
import ca.uqac.lif.labpal.Region;
import enforcementlab.abc.Property1;
import enforcementlab.abc.Property2;
import enforcementlab.abc.Property3;
import enforcementlab.casino.CasinoLolaPolicy;
import enforcementlab.casino.CasinoPolicy;
import enforcementlab.file.AllFilesLifecycle;
import enforcementlab.museum.MuseumPolicy;

public class PolicyProvider
{
	public Processor get(Region r)
	{
		String policy = r.getString(GateExperiment.POLICY);
		if (policy == null)
		{
			return null;
		}
		switch (policy)
		{
		case Property1.NAME:
			return new Property1();
		case Property2.NAME:
			return new Property2();
		case Property3.NAME:
			return new Property3();
		case AllFilesLifecycle.NAME:
			return new AllFilesLifecycle();
		case MuseumPolicy.NAME:
			return new MuseumPolicy();
		case CasinoPolicy.NAME:
			return new CasinoPolicy(0);
		}
		return null;
	}
}
