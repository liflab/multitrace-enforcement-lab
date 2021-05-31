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
package multitrace;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import multitrace.Quadrilean.Value;

public abstract class Filter extends SynchronousProcessor implements Checkpointable
{
	List<PrefixTreeElement> m_elements;

	protected Processor m_mu;

	protected Processor m_checkpoint;

	protected Processor m_current;

	protected Endpoint<Event,Value> m_checkpointEndpoint;

	public Filter(Processor mu)
	{
		super(1, 1);
		m_mu = mu;
		m_checkpoint = m_mu.duplicate();
		m_current = m_mu.duplicate();
		m_elements = new ArrayList<PrefixTreeElement>();
		m_checkpointEndpoint = new Endpoint<Event,Value>(m_checkpoint);
	}

	@Override
	public void apply(List<Event> events)
	{
		for (Event e : events)
		{
			if (e != Event.EPSILON)
			{
				m_checkpointEndpoint.getLastValue(e);
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
		List<Endpoint<Event,Value>> endpoints = new ArrayList<Endpoint<Event,Value>>();
		PrefixTreeElement first = m_elements.get(0);
		for (int i = 0; i < first.get(0).size(); i++)
		{
			Endpoint<Event,Value> ep = new Endpoint<Event,Value>(m_current.duplicate(true));
			endpoints.add(ep);
		}
		for (int j = 0; j < m_elements.size(); j++)
		{
			PrefixTreeElement mte = m_elements.get(j);
			List<Endpoint<Event,Value>> new_endpoints = new ArrayList<Endpoint<Event,Value>>();
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
						verdict = new_ep.getLastValue(e);
					}
					if (verdict == Value.FALSE)
					{
						events_to_add.add(Event.DIAMOND);
					}
					else
					{
						if (j == m_elements.size() - 1 && verdict == Value.P_FALSE)
						{
							// Last event; if it evaluates to "possibly false", don't consider it
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
			endpoints = new_endpoints;
			out_list.add(out_element);
		}
		for (PrefixTreeElement out_elem : out_list)
		{
			outputs.add(new Object[] {out_elem});
		}
		m_elements.clear();
		System.out.println("Filter outputting " + out_list);
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
