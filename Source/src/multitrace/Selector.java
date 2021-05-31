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
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import multitrace.Quadrilean.Value;

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
			m_rankingCheckpoint.getLastValue(e);
		}
		m_rankingEndpoint = new Endpoint<Event,Number>(m_rankingCheckpoint.m_processor.duplicate(true));
		m_elements.clear();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		PrefixTreeElement elem = (PrefixTreeElement) inputs[0];
		System.out.println("Called on" + inputs[0]);
		m_elements.add(elem);
		if (!decide())
		{
			return true;
		}
		List<Endpoint<Event,Number>> endpoints = new ArrayList<Endpoint<Event,Number>>();
		PrefixTreeElement first = m_elements.get(0);
		for (int i = 0; i < first.get(0).size(); i++)
		{
			endpoints.add(m_rankingEndpoint.duplicate());
		}
		for (int len = 0; len < m_elements.size(); len++)
		{
			List<Endpoint<Event,Number>> new_endpoints = new ArrayList<Endpoint<Event,Number>>();
			PrefixTreeElement t_me = m_elements.get(len);
			for (int j = 0; j < t_me.size(); j++)
			{
				Endpoint<Event,Number> ep = endpoints.get(j);
				MultiEvent me = t_me.get(j);
				for (int i = 0; i < me.size(); i++)
				{
					Event e = me.get(i);
					Endpoint<Event,Number> n_ep = ep.duplicate();
					if (e != Event.EPSILON)
					{
						n_ep.getLastValue(e);
					}
					if (len < m_elements.size() - 1 || e != Event.DIAMOND)
					{
						// Don't use this trace
						new_endpoints.add(n_ep);
					}
				}
			}
			endpoints = new_endpoints;
		}

		// Find the endpoint with the highest score
		boolean found = false;
		float best_score = 0;
		Endpoint<Event,Number> best_endpoint = null;
		for (Endpoint<Event,Number> ep : endpoints)
		{
			Number n = ep.getLastValue();
			if (n != null)
			{
				float score = n.floatValue();
				if (!found || score > best_score)
				{
					best_score = score;
					best_endpoint = ep;
					found = true;
				}
			}
		}
		if (best_endpoint != null)
		{
			List<Event> to_output = best_endpoint.getInputTrace();
			System.out.println("Best:" + to_output);
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
		m_elements.clear();
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
