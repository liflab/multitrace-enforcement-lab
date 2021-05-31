package multitrace;

import ca.uqac.lif.cep.Processor;

public class IntervalSelector extends Selector
{
	protected int m_interval;
	
	public IntervalSelector(Processor rho, int interval)
	{
		super(rho);
		m_interval = interval;
	}

	@Override
	protected boolean decide()
	{
		return m_elements.size() == m_interval;
	}
}
