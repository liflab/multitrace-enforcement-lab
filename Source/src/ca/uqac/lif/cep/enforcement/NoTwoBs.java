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

import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.functions.Constant;

/**
 * Moore machine used as a monitor for the property: a trace cannot contain
 * two successive <i>b</i>.
 */
public class NoTwoBs extends StateMooreMachine
{
	/**
	 * Creates a new instance of the monitor.
	 */
	public NoTwoBs()
	{
		super(1, 1);
		addTransition(0, new EventTransition("b", 1));
		addTransition(1, new EventTransition("b", 2));
		addTransition(0, new TransitionOtherwise(0));
		addTransition(1, new TransitionOtherwise(0));
		addTransition(2, new TransitionOtherwise(2));
		addSymbol(0, new Constant(Quadrilean.Value.P_TRUE));
		addSymbol(1, new Constant(Quadrilean.Value.P_TRUE));
		addSymbol(2, new Constant(Quadrilean.Value.FALSE));
	}
}
