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
import ca.uqac.lif.cep.enforcement.Quadrilean.QuadrileanCast;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tkltl.OperatorC;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;

public class MinimizeBetsOutsideGame extends OperatorC
{
	public MinimizeBetsOutsideGame()
	{
		super(getCondition(), Quadrilean.Value.TRUE);
	}

	protected static GroupProcessor getCondition()
	{
		GroupProcessor n_eq_e = new GroupProcessor(1, 1);
		Fork f = new Fork(3);
		OperatorC new_game = new OperatorC(new ApplyFunction(CasinoFunction.isStart), Quadrilean.Value.TRUE);
		Connector.connect(f, 0, new_game, 0);
		OperatorC end_game = new OperatorC(new ApplyFunction(CasinoFunction.isEnd), Quadrilean.Value.TRUE);
		Connector.connect(f, 1, end_game, 0);
		ApplyFunction is_bet = new ApplyFunction(CasinoFunction.isBet);
		Connector.connect(f, 2, is_bet, 0);
		ApplyFunction equals = new ApplyFunction(new FunctionTree(QuadrileanCast.instance,
				new FunctionTree(Booleans.and, StreamVariable.Z,
						new FunctionTree(Equals.instance, StreamVariable.X, StreamVariable.Y))));
		Connector.connect(new_game, 0, equals, 0);
		Connector.connect(end_game, 0, equals, 1);
		Connector.connect(is_bet, 0, equals, 2);
		n_eq_e.addProcessors(f, new_game, end_game, is_bet, equals);
		n_eq_e.associateInput(0, f, 0);
		n_eq_e.associateOutput(0, equals, 0);
		return n_eq_e;
	}
}
