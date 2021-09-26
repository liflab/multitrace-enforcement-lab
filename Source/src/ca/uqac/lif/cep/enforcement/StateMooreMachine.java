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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.ContextAssignment;

/**
 * A {@link MooreMachine} that exposes its current internal state. This
 * processor does nothing more than BeepBeep's original implementation
 * in the LTL palette, with the exception of method {@link #getCurrentState()}
 * which makes it possible to query the machine's internal state at any
 * moment.
 * <p>
 * Method {@link #duplicate(boolean)} also has to be overridden so that it
 * returns a {@link StateMooreMachine} instead of a {@link MooreMachine}.
 */
public class StateMooreMachine extends MooreMachine
{
	/**
	 * The constant "true" produced as output by the machine.
	 */
	public static final transient Constant TRUE = new Constant(true);
	
	/**
	 * The constant "false" produced as output by the machine.
	 */
	public static final transient Constant FALSE = new Constant(false);
	
	public StateMooreMachine(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
	}

	/**
	 * Gets the current state of the Moore machine.
	 * @return The current state 
	 */
	public int getCurrentState()
	{
		return m_currentState;
	}

	@Override
	public StateMooreMachine duplicate(boolean with_state)
	{
		StateMooreMachine out = new StateMooreMachine(getInputArity(), getOutputArity());
		out.m_initialState = m_initialState;
		out.m_outputSymbols = m_outputSymbols;
		out.m_relation = new HashMap<Integer,List<Transition>>();
		for (int k : m_relation.keySet())
		{
			List<Transition> lt = m_relation.get(k);
			List<Transition> new_lt = new ArrayList<Transition>();
			for (Transition t : lt)
			{
				new_lt.add(t.duplicate(with_state));
			}
			out.m_relation.put(k, new_lt);
		}		
		if (with_state)
		{
			out.setContext(m_context);
			out.m_currentState = m_currentState;
			out.m_lastOccurrences.putAll(m_lastOccurrences);
			out.m_looplessPath.addAll(m_looplessPath);
		}
		else
		{
			for (ContextAssignment ca : m_initialAssignments)
			{
				out.addInitialAssignment(ca);
			}	
		}
		return out;
	}
	
	/**
	 * Transition of a {@link MooreMachine} that fires if the incoming event
	 * is equal to the transition's label.
	 */
	public static class EventTransition extends MooreMachine.Transition
	{
		/**
		 * The event to compare with.
		 */
		protected Event m_event;
		
		/**
		 * The destination state.
		 */
		protected int m_destination;
		
		/**
		 * Creates a new transition.
		 * @param e The event to compare with
		 * @param destination The destination state
		 */
		public EventTransition(Event e, int destination)
		{
			super();
			m_event = e;
			m_destination = destination;
		}
		
		/**
		 * Creates a new transition.
		 * @param label The event label to compare with
		 * @param destination The destination state
		 */
		public EventTransition(String label, int destination)
		{
			this(Event.get(label), destination);
		}
		
		@Override
		public boolean isFired(Object[] inputs, Context context)
		{
			if (inputs[0] instanceof Event && ((Event) inputs[0]).equals(m_event))
			{
				return true;
			}
			return false;
		}
		
		@Override
		public String toString()
		{
			return m_event + "->" + m_destination;
		}
		
		@Override
		public int getDestination()
		{
			return m_destination;
		}
		
		@Override
		public EventTransition duplicate(boolean with_state)
		{
			return new EventTransition(m_event, m_destination);
		}
	}
}
