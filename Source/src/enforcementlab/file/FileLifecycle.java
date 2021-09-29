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

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.Quadrilean;
import ca.uqac.lif.cep.enforcement.StateMooreMachine;
import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.functions.Constant;

public class FileLifecycle extends StateMooreMachine
{
	protected static final transient Constant C_FALSE = new Constant(Quadrilean.Value.FALSE);
	protected static final transient Constant C_P_FALSE = new Constant(Quadrilean.Value.P_FALSE);
	protected static final transient Constant C_P_TRUE = new Constant(Quadrilean.Value.P_TRUE);

	public FileLifecycle()
	{
		super(1, 1);
		addTransition(0, new HasName("Open", 1));
		addTransition(1, new HasName("Close", 0));
		addTransition(1, new HasName("Write", 2));
		addTransition(1, new HasName("Read", 3));
		addTransition(2, new HasName("Close", 0));
		addTransition(2, new HasName("Write", 2));
		addTransition(2, new HasName("Read", 3));
		addTransition(3, new HasName("Read", 3));
		addTransition(3, new HasName("Close", 0));
		addTransition(0, new TransitionOtherwise(4));
		addTransition(1, new TransitionOtherwise(4));
		addTransition(2, new TransitionOtherwise(4));
		addTransition(3, new TransitionOtherwise(4));
		addTransition(4, new TransitionOtherwise(4));
		addSymbol(0, C_P_TRUE);
		addSymbol(1, C_P_TRUE);
		addSymbol(2, C_P_TRUE);
		addSymbol(3, C_P_TRUE);
		addSymbol(4, C_FALSE);
	}

	protected static class HasName extends Transition
	{
		protected int m_destination;
		
		protected String m_name;
		
		public HasName(String name, int destination)
		{
			super();
			m_name = name;
			m_destination = destination;
		}
		
		@Override
		public boolean isFired(Object[] inputs, Context c)
		{
			Event e = (Event) inputs[0];
			String[] parts = e.getLabel().split(" ");
			return parts[0].compareTo(m_name) == 0;
		}
		
		@Override
		public int getDestination()
		{
			return m_destination;
		}
		
		@Override
		public HasName duplicate(boolean with_state)
		{
			return new HasName(m_name, m_destination);
		}
	}
}
