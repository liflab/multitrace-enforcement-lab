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

import java.util.Queue;

import ca.uqac.lif.cep.SynchronousProcessor;

public class AppendToMultiTrace extends SynchronousProcessor
{
	protected int m_children;
	
	public AppendToMultiTrace()
	{
		super(1, 1);
		m_children = 1;
	}
	
	@Override
	public void reset()
	{
		m_children = 1;
	}

	@Override
	protected boolean compute(Object[] input, Queue<Object[]> output)
	{
		MultiEvent me = (MultiEvent) input[0];
		MultiTraceElement mte = new MultiTraceElement();
		for (int i = 0; i < m_children; i++)
		{
			mte.add(me);
		}
		output.add(new Object[] {mte});
		m_children *= me.size();
		return true;
	}

	@Override
	public AppendToMultiTrace duplicate(boolean with_state)
	{
		AppendToMultiTrace a = new AppendToMultiTrace();
		if (with_state)
		{
			a.m_children = m_children;
		}
		return a;
	}
}
