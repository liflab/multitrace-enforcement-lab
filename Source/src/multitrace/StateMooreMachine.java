package multitrace;

import ca.uqac.lif.cep.fsm.MooreMachine;

/**
 * A {@link MooreMachine} that exposes its current internal state.
 */
public class StateMooreMachine extends MooreMachine
{
	public StateMooreMachine(int in_arity, int out_arity)
	{
		super(in_arity, out_arity);
	}
	
	public int getCurrentState()
	{
		return m_currentState;
	}
	
	@Override
	public StateMooreMachine duplicate(boolean with_state)
	{
		// TODO
		return null;
	}
}
