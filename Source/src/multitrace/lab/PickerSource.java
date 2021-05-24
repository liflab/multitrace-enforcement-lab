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
package multitrace.lab;

import java.util.Queue;

import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.synthia.Picker;

/**
 * A source of events obtained by an underlying {@link Picker}.
 * 
 * @param <T> The type of the events produced
 */
public class PickerSource<T> extends Source
{
	/**
	 * The picker used to produce random multi-events.
	 */
	protected Picker<T> m_picker;
	
	/**
	 * The number of events the source is asked to produce.
	 */
	protected int m_length;
	
	/**
	 * A counter keeping track of the number of events generated so far.
	 */
	protected int m_eventCount;
	
	/**
	 * Creates a new picker source.
	 * @param mep The picker used to produce random events
	 * @param length The number of events the source is asked to produce
	 */
	public PickerSource(Picker<T> mep, int length)
	{
		super(1);
		m_picker = mep;
		m_length = length;
	}

	@Override
	protected boolean compute(Object[] input, Queue<Object[]> output)
	{
		if (m_eventCount >= m_length)
		{
			return false;
		}
		m_eventCount++;
		output.add(new Object[] {m_picker.pick()});
		return true;
	}

	@Override
	public PickerSource<T> duplicate(boolean with_state)
	{
		PickerSource<T> res = new PickerSource<T>(m_picker.duplicate(with_state), m_length);
		if (with_state)
		{
			res.m_eventCount = m_eventCount;
		}
		return res;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		m_picker.reset();
		m_eventCount = 0;
	}
}
