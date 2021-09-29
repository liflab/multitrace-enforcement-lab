package enforcementlab.file;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.fsm.MooreMachine;

public class FileLifecycle extends MooreMachine
{

	public FileLifecycle()
	{
		super(1, 1);
		addTransition(0, new HasName("Open", 1));
		addTransition(1, new HasName("Write", 2));
		addTransition(2, new HasName("Write", 2));
		addTransition(1, new HasName("Read", 3));
		addTransition(3, new HasName("Read", 3));
		addTransition(3, new HasName("Close", 0));
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
	}
}
