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

/**
 * Takes as input a trace of events, and produces a sequence of
 * multi-trace elements.
 */
public class Proxy extends SynchronousProcessor implements Checkpointable
{
	/**
	 * The processor producing multi-events.
	 */
	protected Processor m_pi;
	
	/**
	 * The processor in its last checkpointed state
	 */
	protected Processor m_checkpoint;
	
	/**
	 * The endpoint used to generate the prefix tree elements.
	 */
	protected Endpoint<Event,MultiEvent> m_endpoint;
	
	/**
	 * The endpoint used to update the checkpoint.
	 */
	protected Endpoint<Event,MultiEvent> m_checkpointEndpoint;
	
	/**
	 * A counter for the number of children to generate in the next event.
	 */
	protected int m_children;
	
	/**
	 * Creates a new proxy.
	 * @param proxy The processor producing multi-events
	 */
	public Proxy(Processor pi)
	{
		super(1, 1);
		m_children = 1;
		m_pi = pi;
		m_checkpoint = m_pi.duplicate();
		m_endpoint = new Endpoint<Event,MultiEvent>(m_pi.duplicate());
		m_checkpointEndpoint = new Endpoint<Event,MultiEvent>(m_checkpoint);
	}

	@Override
	public void apply(List<Event> events)
	{
		m_children = 1;
		for (Event e : events)
		{
			m_checkpointEndpoint.getLastValue(e);
		}
		m_endpoint = new Endpoint<Event,MultiEvent>(m_checkpointEndpoint.m_processor.duplicate(true));
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		List<MultiEvent> l_me = m_endpoint.getStream((Event) inputs[0]);
		List<PrefixTreeElement> ptes = new ArrayList<PrefixTreeElement>();
		for (int j = 0; j < l_me.size(); j++)
		{
			PrefixTreeElement mte = new PrefixTreeElement();
			MultiEvent me = l_me.get(j);
			for (int i = 0; i < m_children; i++)
			{
				mte.add(me);
			}
			ptes.add(mte);
			m_children *= me.size();
		}
		outputs.add(new Object[] {ptes});
		return true;
	}

	@Override
	public Processor duplicate(boolean arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
