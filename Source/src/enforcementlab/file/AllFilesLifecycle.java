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
package enforcementlab.file;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.enforcement.Quadrilean;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Maps;

public class AllFilesLifecycle extends GroupProcessor
{
	public AllFilesLifecycle()
	{
		super(1, 1);
		Slice s = new Slice(GetFilename.instance, new FileLifecycle());
		ApplyFunction af = new ApplyFunction(new FunctionTree(Quadrilean.BagAnd.instance, Maps.values));
		addProcessors(s, af);
		Connector.connect(s, af);
		associateInput(0, s, 0);
		associateOutput(0, af, 0);
	}
}
