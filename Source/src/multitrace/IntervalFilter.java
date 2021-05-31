package multitrace;

import ca.uqac.lif.cep.Processor;

public class IntervalFilter extends Filter
{
	protected int m_interval;
	
	public IntervalFilter(Processor mu, int interval)
	{
		super(mu);
		m_interval = interval;
	}

	@Override
	protected boolean decide()
	{
		return m_elements.size() == m_interval;
	}
}
