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

public class BruteForceMultiTraceSelector extends MultiTraceSelector
{
	public BruteForceMultiTraceSelector(Processor monitor)
	{
		super(monitor);
	}

	@Override
	protected boolean compute(Object[] input, Queue<Object[]> output)
	{
		m_pending.add((MultiTraceElement) input[0]);
		if (!decide())
		{
			return true;
		}
		List<List<Event>> traces = deploy();
		Endpoint<Event,Number> best = null;
		int best_score = Integer.MIN_VALUE;
		for (List<Event> trace : traces)
		{
			Endpoint<Event,Number> ep = new Endpoint<Event,Number>(m_monitor.duplicate(true));
			for (Event e : trace)
			{
				ep.getVerdict(e);
			}
			int score = ep.getLastValue().intValue();
			if (score > best_score)
			{
				best = ep;
			}
		}
		if (best != null)
		{
			m_monitor = best.m_processor;
			List<Event> to_output = best.getInputTrace();
			for (Event e : to_output)
			{
				output.add(new Object[] {e});
			}
			m_prefix.addAll(to_output);
			if (m_outerPipeline != null && !to_output.isEmpty())
			{
				m_outerPipeline.apply(to_output);
			}
			m_score = best.getLastValue().intValue();	
		}
		m_pending.clear();
		return true;
	}
	
	/**
	 * Transforms a list of multi-trace elements into a list of
	 * uni-traces.
	 * @return The list of unitraces
	 */
	protected List<List<Event>> deploy()
	{
		List<List<Event>> out = new ArrayList<List<Event>>();
		out.add(new ArrayList<Event>());
		List<List<Event>> new_out = new ArrayList<List<Event>>();
		for (int i = 0; i < m_pending.size(); i++)
		{
			MultiTraceElement mte = m_pending.get(i);
			for (int j = 0; j < mte.size(); j++)
			{
				MultiEvent me = mte.get(j);
				List<Event> prefix = out.get(j);
				for (Event e : me)
				{
					List<Event> new_list = new ArrayList<Event>(prefix.size() + 1);
					new_list.addAll(prefix);
					new_list.add(e);
					new_out.add(new_list);
				}
			}
			out = new_out;
		}
		return out;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
