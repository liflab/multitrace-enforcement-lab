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
package enforcementlab.casino;

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * A processor that calls an internal processor by forcing pull mode.
 * This "wrapper" processor is required, since the LOLA interpreter from
 * the Polyglot library only works in pull mode, while the enforcement
 * pipeline in the current lab works in push mode.
 */
public class ProcessorPullWrapper extends SynchronousProcessor
{
	protected ClearableSource m_source;
	
	protected Processor m_processor;
	
	protected Pullable m_pullable;
	
	public ProcessorPullWrapper(Processor p)
	{
		super(1, 1);
		m_source = new ClearableSource();
		m_source.loop(false);
		m_processor = p;
		Connector.connect(m_source, m_processor);
		m_pullable = p.getPullableOutput();
	}

	@Override
	public ProcessorPullWrapper duplicate(boolean with_state)
	{
		ProcessorPullWrapper ppw = new ProcessorPullWrapper(m_processor.duplicate(with_state));
		return ppw;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_processor.reset();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		m_source.setEvents(inputs[0]);
		m_source.reset();
		while (m_pullable.hasNext())
		{
			outputs.add(new Object[] {m_pullable.pull()});
		}
		return true;
	}
	
	protected static class ClearableSource extends QueueSource
	{
		@Override
		public ClearableSource setEvents(Object ... events)
		{
			m_events.clear();
			super.setEvents(events);
			return this;
		}
	}
}
