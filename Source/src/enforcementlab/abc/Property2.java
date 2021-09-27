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
package enforcementlab.abc;

import ca.uqac.lif.cep.enforcement.Quadrilean;
import ca.uqac.lif.cep.enforcement.StateMooreMachine;
import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.functions.Constant;

/**
 * Monitor for the property: "all a's come in successive pairs".
 */
public class Property2 extends StateMooreMachine
{
	public static final transient String NAME = "Stuttering a's";
	
	public Property2()
	{
		super(1, 1);
		addTransition(0, new EventTransition("a", 1));
		addTransition(0, new TransitionOtherwise(0));
		addTransition(1, new EventTransition("a", 0));
		addTransition(1, new TransitionOtherwise(2));
		addTransition(2, new TransitionOtherwise(2));
		addSymbol(0, new Constant(Quadrilean.Value.P_TRUE));
		addSymbol(1, new Constant(Quadrilean.Value.P_FALSE));
		addSymbol(2, new Constant(Quadrilean.Value.FALSE));
	}
}
