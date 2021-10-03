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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.enforcement.Quadrilean;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tkltl.OperatorC;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Numbers;

public class MaximizeGames extends GroupProcessor
{
	/**
	 * The name of this scoring processor.
	 */
	public static final transient String NAME = "Maximize games";
	
	public MaximizeGames()
	{
		super(1, 1);
		Fork f = new Fork();
		OperatorC start = new OperatorC(new ApplyFunction(CasinoFunction.isStart), Quadrilean.Value.TRUE);
		OperatorC end = new OperatorC(new ApplyFunction(CasinoFunction.isEnd), Quadrilean.Value.TRUE);
		Connector.connect(f, 0, start, 0);
		Connector.connect(f, 1, end, 0);
		ApplyFunction minus = new ApplyFunction(Numbers.subtraction);
		Connector.connect(start, 0, minus, 0);
		Connector.connect(end, 0, minus, 1);
		addProcessors(f, start, end, minus);
		associateInput(0, f, 0);
		associateOutput(0, minus, 0);
	}
}
