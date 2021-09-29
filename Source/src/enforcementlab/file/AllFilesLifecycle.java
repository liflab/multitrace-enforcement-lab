package enforcementlab.file;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.Event;

public class AllFilesLifecycle extends GroupProcessor
{
	public AllFilesLifecycle()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event e = (Event) inputs[0];
		
		return false;
	}

	@Override
	public Processor duplicate(boolean arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
