package enforcementlab.file;

import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.functions.UnaryFunction;

public class GetFilename extends UnaryFunction<Event,Integer>
{
	/**
	 * A single visible instance of the function.
	 */
	public static final transient GetFilename instance = new GetFilename();
	
	protected GetFilename()
	{
		super(Event.class, Integer.class);
	}

	@Override
	public Integer getValue(Event e)
	{
		String[] parts = e.getLabel().split(" ");
		return Integer.parseInt(parts[1]);
	}
}
