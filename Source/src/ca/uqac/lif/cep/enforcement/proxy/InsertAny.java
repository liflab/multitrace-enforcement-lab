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
import ca.uqac.lif.cep.enforcement.Event.AddedEvent;
import ca.uqac.lif.cep.enforcement.MultiEvent;
import ca.uqac.lif.cep.enforcement.MultiTraceElement;

/**
 * Proxy that is allowed to insert any event from a set at any point in
 * an execution.
 */
public class InsertAny extends UniformProcessor
{
	/**
	 * The name of this proxy.
	 */
	public static final transient String NAME = "Insert any";
	
	/**
	 * The list of events that can be inserted at any time.
	 */
	protected List<Event> m_insertable;
	
	/**
	 * Creates a new instance of the proxy.
	 * @param events The list of events that can be inserted at any time
	 */
	public InsertAny(List<Event> events)
	{
		super(1, 1);
		m_insertable = new ArrayList<Event>();
		for (Event e : events)
		{
			if (e instanceof AddedEvent)
			{
				m_insertable.add((AddedEvent) e);
			}
			else
			{
				m_insertable.add(Event.getAdded(e.getLabel()));
			}
		}
	}
	
	/**
	 * Creates a new instance of the proxy.
	 * @param events The events that can be inserted at any time
	 */
	public InsertAny(Event ... events)
	{
		this(Arrays.asList(events));
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		Event in_e = (Event) inputs[0];
		MultiTraceElement mte = new MultiTraceElement();
		MultiEvent me = new MultiEvent(Event.EPSILON);
		for (Event e : m_insertable)
		{
			me.add(e);
		}
		mte.add(me);
		mte.add(new MultiEvent(in_e));
		outputs[0] = mte;
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		return new InsertAny(m_insertable);
	}
}
