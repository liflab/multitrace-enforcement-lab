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
package enforcementlab.file;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.sequence.MarkovChain;
import ca.uqac.lif.synthia.util.Constant;
import enforcementlab.PickerSource;

/**
 * A source of events from the file manipulation scenario.
 */
public class FileSource extends PickerSource<Event>
{
	/**
	 * The name of this event source.
	 */
	public static final transient String NAME = "Files";
	
	/**
	 * The maximum number of files that are manipulated in the simulated
	 * trace.
	 */
	protected static final transient int s_numFiles = 4;
	
	public FileSource(Picker<Float> float_source, float prob_error, int length)
	{
		super(new FileEventPicker(float_source, prob_error, s_numFiles), length);
	}
	
	public static List<Event> getAlphabet()
	{
		List<Event> alphabet = new ArrayList<Event>();
		for (int i = 0; i < s_numFiles; i++)
		{
			alphabet.add(Event.get("Open " + i));
			alphabet.add(Event.get("Close " + i));
			alphabet.add(Event.get("Read " + i));
			alphabet.add(Event.get("Write " + i));
		}
		return alphabet;
	}
	
	/**
	 * A picker of file events.
	 */
	protected static class FileEventPicker implements Picker<Event>
	{
		/**
		 * The number of distinct files interleaved in the output trace.
		 */
		protected int m_numFiles;
		
		protected List<MarkovChain<Event>> m_chains;
		
		protected Picker<Float> m_floatSource;
		
		protected float m_probError;
		
		public FileEventPicker(Picker<Float> float_source, float prob_error, int num_files)
		{
			super();
			m_floatSource = float_source;
			m_numFiles = num_files;
			m_chains = new ArrayList<MarkovChain<Event>>(m_numFiles);
			m_probError = prob_error;
			for (int i = 0; i < m_numFiles; i++)
			{
				m_chains.add(new FileMarkovChain(i, float_source, prob_error));
			}
		}
		
		@Override
		public FileEventPicker duplicate(boolean with_state)
		{
			FileEventPicker fep = new FileEventPicker(m_floatSource.duplicate(with_state), m_probError, m_numFiles);
			if (with_state)
			{
				for (MarkovChain<Event> chain : m_chains)
				{
					fep.m_chains.add(chain.duplicate(with_state));
				}
			}
			return fep;
		}

		@Override
		public Event pick()
		{
			// Select file
			int file_nb = (int) (m_floatSource.pick() * (float) m_numFiles);
			MarkovChain<Event> chain = m_chains.get(file_nb);
			return chain.pick();
		}

		@Override
		public void reset()
		{
			m_floatSource.reset();
			for (MarkovChain<?> chain : m_chains)
			{
				chain.reset();
			}
		}
	}
	
	protected static class FileMarkovChain extends MarkovChain<Event>
	{
		public FileMarkovChain(int file_nb, Picker<Float> float_source, float prob_error)
		{
			super(float_source);
			float prob_ok = 1 - prob_error;
			add(0, 1, prob_ok);
			add(1, 2, 0.33);
			add(1, 3, 0.33);
			add(1, 0, 0.34);
			add(2, 3, prob_ok);
			add(3, 0, prob_ok);
			add(0, new Constant<Event>(Event.get("Close " + file_nb)));
			add(1, new Constant<Event>(Event.get("Open " + file_nb)));
			add(2, new Constant<Event>(Event.get("Write " + file_nb)));
			add(3, new Constant<Event>(Event.get("Read " + file_nb)));
			add(0, 2, prob_error / 2);
			add(0, 3, prob_error / 2);
			add(3, 1, prob_error / 2);
			add(3, 2, prob_error / 2);
		}
	}
}
