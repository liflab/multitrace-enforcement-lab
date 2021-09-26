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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.enforcement.Event.Deleted;

public class EventEndpoint<T> extends Endpoint<Event,T>
{

	public EventEndpoint(Processor monitor)
	{
		super(monitor);
	}
	
	@SuppressWarnings("unchecked")
	public T getLastValue(Event e)
	{
		m_inputTrace.add(e);
		if (e != null && !e.getLabel().isEmpty() && !(e instanceof Deleted))
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
	public List<T> getStream(Event e)
	{
		m_inputTrace.add(e);
		if (e != null && !e.getLabel().isEmpty() && !(e instanceof Deleted))
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
	
	@SuppressWarnings("unchecked")
	public T getVerdict(Event e)
	{
		m_inputTrace.add(e);
		if (!e.getLabel().isEmpty() && !(e instanceof Deleted))
		{
			m_pushable.push(e);
		}
		Queue<?> q = m_sink.getQueue();
		if (q.isEmpty())
		{
			return m_lastValue;
		}
		T verdict = (T) q.remove();
		m_lastValue = verdict;
		return verdict;
	}
	
	@Override
	public EventEndpoint<T> duplicate()
	{
		EventEndpoint<T> e = new EventEndpoint<T>(m_processor.duplicate(true));
		e.m_lastValue = m_lastValue;
		e.m_inputTrace.addAll(m_inputTrace);
		return e;
	}

}
