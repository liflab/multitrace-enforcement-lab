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
package ca.uqac.lif.cep.enforcement.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.cep.enforcement.MultiTraceElement;

/**
 * Proxy that is allowed to delete any event from a set at any point in
 * an execution.
 */
public class DeleteAny extends UniformProcessor
{
	/**
	 * The name of this proxy.
	 */
	public static final transient String NAME = "Delete any";
	
	/**
	 * The list of events that can be deleted at any time.
	 */
	protected List<Event> m_deletable;
	
	/**
	 * Creates a new instance of the proxy.
	 * @param events The list of events that can be inserted at any time
	 */
	public DeleteAny(List<Event> events)
	{
		super(1, 1);
		m_deletable = events;
	}
	
	/**
	 * Creates a new instance of the proxy.
	 * @param events The events that can be inserted at any time
	 */
	public DeleteAny(Event ... events)
	{
		this(Arrays.asList(events));
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event in_e = (Event) inputs[0];
		List<MultiTraceElement> out_list = new ArrayList<MultiTraceElement>();
		MultiTraceElement mte1 = new MultiTraceElement();
		mte1.add(new MultiEvent(in_e));
		out_list.add(mte1);
		if (m_deletable.contains(in_e))
		{
			// One of the events we can delete
			MultiTraceElement mte2 = new MultiTraceElement();
			mte2.add(new MultiEvent(Event.getDeleted(in_e.getLabel())));
			out_list.add(mte2);
		}
		outputs[0] = out_list;
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		return new DeleteAny(m_deletable);
	}
}
