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

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.tmf.QueueSink;

/**
 * A processor acting as a "classical" enforcement monitor. It takes as a
 * parameter a processor returning multi-trace elements, and picks a single
 * uni-event out of each element.
 *
 */
public class EnforcementMonitor extends SynchronousProcessor
{
	/**
	 * The processor turning the proxy's output into prefix tree elements.
	 */
	/*@ non_null @*/ protected Processor m_monitor;
	
	/**
	 * A {@link Pushable} used to give events to the proxy.
	 */
	/*@ non_null @*/ protected Pushable m_pushable;
	
	/**
	 * The sink that gathers events produced by the proxy.
	 */
	/*@ non_null @*/ protected QueueSink m_sink;
	
	public EnforcementMonitor(/*@ non_null @*/ Processor p)
	{
		super(1, 1);
		m_monitor = p;
		m_pushable = p.getPushableInput();
		m_sink = new QueueSink();
		Connector.connect(m_monitor, m_sink);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		m_pushable.push(inputs[0]);
		Queue<?> q = m_sink.getQueue();
		while (!q.isEmpty())
		{
			Object o = q.remove();
			if (o instanceof MultiTraceElement)
			{
				MultiTraceElement mte = (MultiTraceElement) o;
				for (MultiEvent me : mte)
				{
					outputs.add(new Object[] {me.get(0)});
				}
			}
		}
		return true;
	}

	@Override
	public EnforcementMonitor duplicate(boolean with_state)
	{
		EnforcementMonitor em = new EnforcementMonitor(m_monitor.duplicate(with_state));
		return em;
	}
}
