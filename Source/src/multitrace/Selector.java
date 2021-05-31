package multitrace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;

public abstract class Selector extends SynchronousProcessor implements Checkpointable
{
	protected List<PrefixTreeElement> m_elements;
	
	protected Endpoint<Event,Number> m_rankingEndpoint;
	
	protected Endpoint<Event,Number> m_rankingCheckpoint;
	
	protected Processor m_rho;
	
	public Selector(Processor rho)
	{
		super(1, 1);
		m_rho = rho;
		m_rankingCheckpoint = new Endpoint<Event,Number>(m_rho.duplicate());
		m_rankingEndpoint = new Endpoint<Event,Number>(m_rho.duplicate());
		m_elements = new ArrayList<PrefixTreeElement>();
	}

	@Override
	public void apply(List<Event> events)
	{
		for (Event e : events)
		{
			m_rankingCheckpoint.getVerdict(e);
		}
		m_rankingEndpoint = new Endpoint<Event,Number>(m_rankingCheckpoint.m_processor.duplicate(true));
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		PrefixTreeElement elem = (PrefixTreeElement) inputs[0];
		m_elements.add(elem);
		if (!decide())
		{
			return true;
		}
		List<Endpoint<Event,Number>> endpoints = new ArrayList<Endpoint<Event,Number>>();
		endpoints.add(m_rankingEndpoint.duplicate());
		List<Endpoint<Event,Number>> new_endpoints = new ArrayList<Endpoint<Event,Number>>();
		Iterator<PrefixTreeElement> it = m_elements.iterator();
		while (it.hasNext())
		{
			PrefixTreeElement t_me = it.next();
			it.remove();
			for (int j = 0; j < t_me.size(); j++)
			{
				Endpoint<Event,Number> ep = endpoints.get(j);
				MultiEvent me = t_me.get(j);
				for (int i = 0; i < me.size(); i++)
				{
					Event e = me.get(i);
					Endpoint<Event,Number> n_ep = ep.duplicate();
					if (e != Event.EPSILON && e != Event.DIAMOND)
					{
						n_ep.getVerdict(e);
					}
					new_endpoints.add(n_ep);
				}
			}
			endpoints = new_endpoints;
		}
		// Find the endpoint with the highest score
		float best_score = Float.MIN_VALUE;
		Endpoint<Event,Number> best_endpoint = null;
		for (Endpoint<Event,Number> ep : endpoints)
		{
			if (!ep.getInputTrace().contains(Event.DIAMOND))
			{
				float score = ep.getLastValue().floatValue();
				if (score > best_score)
				{
					score = best_score;
					best_endpoint = ep;
				}
			}
		}
		if (best_endpoint != null)
		{
			List<Event> to_output = best_endpoint.getInputTrace();
			// The sequence of uni-events to produce has been computed
			for (Event e : to_output)
			{
				// Output this best event, if it is not the empty event
				if (!e.getLabel().isEmpty())
				{
					outputs.add(new Object[] {e});
				}
			}
		}
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	protected abstract boolean decide();
}
