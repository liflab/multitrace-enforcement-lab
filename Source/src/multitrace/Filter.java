package multitrace;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.ltl.Troolean;

public abstract class Filter extends SynchronousProcessor implements Checkpointable
{
	List<PrefixTreeElement> m_elements;

	protected Processor m_mu;

	protected Processor m_checkpoint;

	protected Processor m_current;

	protected Endpoint<Event,Troolean.Value> m_checkpointEndpoint;

	public Filter(Processor mu)
	{
		super(1, 1);
		m_mu = mu;
		m_checkpoint = m_mu.duplicate();
		m_current = m_mu.duplicate();
		m_elements = new ArrayList<PrefixTreeElement>();
		m_checkpointEndpoint = new Endpoint<Event,Troolean.Value>(m_checkpoint);
	}

	@Override
	public void apply(List<Event> events)
	{
		for (Event e : events)
		{
			if (e != Event.EPSILON)
			{
				m_checkpointEndpoint.getVerdict(e);
			}
		}
		m_current = m_checkpointEndpoint.m_processor.duplicate(true);
		m_elements.clear();
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
		List<PrefixTreeElement> out_list = new ArrayList<PrefixTreeElement>();
		List<Endpoint<Event,Quadrilean.Value>> endpoints = new ArrayList<Endpoint<Event,Quadrilean.Value>>();
		PrefixTreeElement first = m_elements.get(0);
		for (int i = 0; i < first.size(); i++)
		{
			Endpoint<Event,Quadrilean.Value> ep = new Endpoint<Event,Quadrilean.Value>(m_current.duplicate(true));
			endpoints.add(ep);
		}
		for (PrefixTreeElement mte : m_elements)
		{
			List<Endpoint<Event,Quadrilean.Value>> new_endpoints = new ArrayList<Endpoint<Event,Quadrilean.Value>>();
			PrefixTreeElement out_element = new PrefixTreeElement();
			for (int i = 0; i < mte.size(); i++)
			{
				Endpoint<Event,Quadrilean.Value> ep = endpoints.get(i);
				MultiEvent me = mte.get(i);
				List<Event> events_to_add = new ArrayList<Event>(me.size());
				for (Event e : me)
				{
					Endpoint<Event,Quadrilean.Value> new_ep = ep.duplicate();
					Quadrilean.Value verdict = new_ep.getLastValue();
					if (e != Event.EPSILON)
					{
						verdict = new_ep.getVerdict(e);
					}
					if (verdict == Quadrilean.Value.FALSE)
					{
						events_to_add.add(Event.DIAMOND);
					}
					else
					{
						if (i == mte.size() - 1 && verdict == Quadrilean.Value.P_FALSE)
						{
							// Last event; if it evaluates to "possibly false", don't add it
							events_to_add.add(Event.DIAMOND);
						}
						else
						{
							events_to_add.add(e);
						}
					}
					new_endpoints.add(new_ep);
				}
				out_element.add(new MultiEvent(events_to_add));
			}
			out_list.add(out_element);
		}
		for (PrefixTreeElement out_elem : out_list)
		{
			outputs.add(new Object[] {out_elem});
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
