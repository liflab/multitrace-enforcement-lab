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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.SinkLast;

/**
 * An instance of a processor in a particular state. An event can be
 * pushed to this processor using {@link #getVerdict(Event)}, and the
 * result it produces can be queried by the return value of this method.
 * An endpoint can also be duplicated, which produces a distinct copy
 * of the endpoint in the same state as the original.
 *
 * @param <T> The type of the inner processor's output events
 */
public class Endpoint<T>
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
	protected SinkLast m_sink;
	
	/**
	 * The last value produced by the inner processor.
	 */
	protected T m_lastValue = null;

	/**
	 * Creates a new endpoint.
	 * @param monitor A processor instance
	 */
	protected Endpoint(Processor monitor)
	{
		super();
		m_processor = monitor;
		m_sink = new SinkLast();
		Connector.connect(m_processor, m_sink);
		m_pushable = m_processor.getPushableInput();
	}

	/**
	 * Creates a stateful copy of this endpoint and its inner processor.
	 * @return A new endpoint in the same state as the current one
	 */
	public Endpoint<T> duplicate()
	{
		Endpoint<T> e = new Endpoint<T>(m_processor.duplicate(true));
		e.m_lastValue = m_lastValue;
		return e;
	}

	@SuppressWarnings("unchecked")
	public T getVerdict(Event e)
	{
		if (!e.getLabel().isEmpty())
		{
			m_pushable.push(e);
		}
		Object[] out = m_sink.getLast();
		if (out == null)
		{
			return m_lastValue;
		}
		T verdict = (T) out[0];
		m_lastValue = verdict;
		return verdict;
	}
}