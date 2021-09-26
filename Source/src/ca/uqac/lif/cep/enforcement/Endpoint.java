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
package ca.uqac.lif.cep.enforcement;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.QueueSink;

/**
 * An instance of a processor in a particular state. An event can be
 * pushed to this processor using {@link #getLastValue(Event)}, and the
 * result it produces can be queried by the return value of this method.
 * An endpoint can also be duplicated, which produces a distinct copy
 * of the endpoint in the same state as the original.
 *
 * @param <E> The type of the input events
 * @param <T> The type of the inner processor's output events
 */
public class Endpoint<E,T>
{
	/**
	 * A processor instance.
	 */
	protected Processor m_processor;

	/**
	 * A reference to the monitor's pushable.
	 */
	protected Pushable m_pushable;

	/**
	 * A sink receiving events from the monitor.
	 */
	protected QueueSink m_sink;
	
	/**
	 * The last value produced by the inner processor.
	 */
	protected T m_lastValue = null;
	
	/**
	 * The list of events given to the endpoint
	 */
	protected List<E> m_inputTrace;

	/**
	 * Creates a new endpoint.
	 * @param monitor A processor instance
	 */
	public Endpoint(Processor monitor)
	{
		super();
		m_processor = monitor;
		m_sink = new QueueSink();
		Connector.connect(m_processor, m_sink);
		m_pushable = m_processor.getPushableInput();
		m_inputTrace = new ArrayList<E>();
	}

	/**
	 * Creates a stateful copy of this endpoint and its inner processor.
	 * @return A new endpoint in the same state as the current one
	 */
	public Endpoint<E,T> duplicate()
	{
		Endpoint<E,T> e = new Endpoint<E,T>(m_processor.duplicate(true));
		e.m_lastValue = m_lastValue;
		e.m_inputTrace.addAll(m_inputTrace);
		return e;
	}

	/**
	 * Feeds an event to the processor in this endpoint, and retrieves its
	 * output.
	 * @param e The event
	 * @return The output from the processor after being fed the event
	 */
	@SuppressWarnings("unchecked")
	public T getLastValue(E e)
	{
		m_inputTrace.add(e);
		if (e != null)
		{
			m_pushable.push(e);
		}
		Queue<Object> q = m_sink.getQueue();
		if (q.isEmpty())
		{
			return m_lastValue;
		}
		T out = null;
		while (!q.isEmpty())
		{
			out = (T) q.remove();
		}
		m_lastValue = out;
		return out;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getStream(E e)
	{
		m_inputTrace.add(e);
		if (e != null)
		{
			m_pushable.push(e);
		}
		Queue<Object> q = m_sink.getQueue();
		List<T> list = new ArrayList<T>(q.size());
		while (!q.isEmpty())
		{
			list.add((T) q.remove());
		}
		return list;
	}
	
	/**
	 * Gets the last value produced by the processor in this endpoint.
	 * @return The value
	 */
	public T getLastValue()
	{
		return m_lastValue;
	}
	
	/**
	 * Gets the sequence of events given to the processor in this endpoint.
	 * @return The sequence
	 */
	public List<E> getInputTrace()
	{
		return m_inputTrace;
	}
	
	/**
	 * Resets the processor of this endpoint.
	 */
	public void reset()
	{
		m_processor.reset();
		m_lastValue = null;
		m_inputTrace.clear();
	}
}