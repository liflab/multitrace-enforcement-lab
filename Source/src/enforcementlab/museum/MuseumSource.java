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
package enforcementlab.museum;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.synthia.Picker;
import enforcementlab.PickerSource;

/**
 * A source of events for the museum scenario.
 * <p>
 * The source keeps track of the number of adults, children and guards inside
 * the museum at any given moment, and semi-randomly selects the next event by
 * first establishing the possible outputs as follows:
 * <ol>
 * <li>A new adult, child or guard can always enter the museum</li>
 * <li>An event "adult out" is possible if the number of adults in the
 * museum is greater than 0; the same for children and guards</li>
 * <li>If no adult is in the museum, the "adult out" event may still be added
 * to the possibilities after tossing a coin; the same for children and
 * guards</li>
 * </ol>
 * A consequence of the last rule is that events that are invalid in the
 * current state of the trace can also be introduced. The probability of the
 * biased coin determines how likely this is to occur. The source can ignore
 * this last condition and only produce valid traces by calling
 * {@link #includeInvalid(boolean)}.
 * <p>
 * Once the possible next events are established, one of them is picked and
 * returned.  
 */
public class MuseumSource extends PickerSource<Event>
{
	/**
	 * The name of this event source.
	 */
	public static final transient String NAME = "Museum";

	/**
	 * The event "adult in".
	 */
	public static final transient Event ADULT_IN = Event.get("Adult in");

	/**
	 * The event "adult out".
	 */
	public static final transient Event ADULT_OUT = Event.get("Adult out");

	/**
	 * The event "child in".
	 */
	public static final transient Event CHILD_IN = Event.get("Child in");

	/**
	 * The event "adult out".
	 */
	public static final transient Event CHILD_OUT = Event.get("Child out");

	/**
	 * The event "guard in".
	 */
	public static final transient Event GUARD_IN = Event.get("Guard in");

	/**
	 * The event "guard out".
	 */
	public static final transient Event GUARD_OUT = Event.get("Guard out");

	public MuseumSource(Picker<Float> float_source, Picker<Boolean> coin, int length)
	{
		super(new MuseumEventPicker(float_source, coin), length);
	}

	/**
	 * Sets whether this source may produce events violating the negative
	 * quantity condition.
	 * @param b Set to {@code true} to include invalid events, {@code false}
	 * otherwise
	 */
	public void includeInvalid(boolean b)
	{
		((MuseumEventPicker) m_picker).includeInvalid(b);
	}

	protected static class MuseumEventPicker implements Picker<Event>
	{
		protected Picker<Float> m_floatSource;

		protected Picker<Boolean> m_coin;

		/**
		 * The number of adults inside the museum in the trace prefix generated
		 * so far.
		 */
		protected int m_numAdults;

		/**
		 * The number of children inside the museum in the trace prefix generated
		 * so far.
		 */
		protected int m_numChildren;

		/**
		 * The number of guards inside the museum in the trace prefix generated
		 * so far.
		 */
		protected int m_numGuards;

		/**
		 * A flag indicating whether the picker may produce erroneous events
		 * (more people leaving than entering).
		 */
		protected boolean m_withErrors = false;

		/**
		 * Creates a new museum event picker.
		 * @param float_source A source of floating-point numbers
		 * @param coin A source of Boolean values
		 */
		public MuseumEventPicker(Picker<Float> float_source, Picker<Boolean> coin)
		{
			super();
			m_floatSource = float_source;
			m_coin = coin;
			m_numAdults = 0;
			m_numChildren = 0;
			m_numGuards = 0;
		}

		/**
		 * Sets whether this picker may produce events violating the negative
		 * quantity condition.
		 * @param b Set to {@code true} to include invalid events, {@code false}
		 * otherwise
		 */
		public void includeInvalid(boolean b)
		{
			m_withErrors = b;
		}

		@Override
		public MuseumEventPicker duplicate(boolean with_state)
		{
			MuseumEventPicker mep = new MuseumEventPicker(m_floatSource.duplicate(with_state), m_coin.duplicate(with_state));
			if (with_state)
			{
				mep.m_numAdults = m_numAdults;
				mep.m_numChildren = m_numChildren;
				mep.m_numGuards = m_numGuards;
			}
			return mep;
		}

		@Override
		public Event pick()
		{
			List<Event> available = new ArrayList<Event>();
			// We give a higher probability to non-guards by adding them
			// to the list multiple times
			available.add(ADULT_IN);
			available.add(ADULT_IN);
			available.add(CHILD_IN);
			available.add(CHILD_IN);
			available.add(GUARD_IN);
			if (m_numAdults > 0 || (m_withErrors && m_coin.pick()))
			{
				available.add(ADULT_OUT);
				available.add(ADULT_OUT);
			}
			if (m_numChildren > 0 || (m_withErrors && m_coin.pick()))
			{
				available.add(CHILD_OUT);
				available.add(CHILD_OUT);
			}
			if (m_numGuards > 0 || (m_withErrors && m_coin.pick()))
			{
				available.add(GUARD_OUT);
				available.add(GUARD_OUT);
			}
			int index = (int) (m_floatSource.pick() * (float) available.size());
			Event picked = available.get(index);
			updateCounts(picked);
			return picked;
		}

		/**
		 * Updates the count of children, adults and guards based on the selected
		 * output event. 
		 * @param e The event
		 */
		protected void updateCounts(Event e)
		{
			if (e.equals(ADULT_IN))
			{
				m_numAdults++;
			}
			else if (e.equals(ADULT_OUT))
			{
				m_numAdults--;
			}
			else if (e.equals(CHILD_IN))
			{
				m_numChildren++;
			}
			else if (e.equals(CHILD_OUT))
			{
				m_numChildren--;
			}
			else if (e.equals(GUARD_IN))
			{
				m_numGuards++;
			}
			else if (e.equals(GUARD_OUT))
			{
				m_numGuards--;
			}
		}

		@Override
		public void reset()
		{
			m_floatSource.reset();
			m_coin.reset();
		}
	}

	public static List<Event> getAlphabet()
	{
		List<Event> alphabet = new ArrayList<Event>(3);
		alphabet.add(ADULT_IN);
		alphabet.add(ADULT_OUT);
		alphabet.add(CHILD_IN);
		alphabet.add(CHILD_OUT);
		alphabet.add(GUARD_IN);
		alphabet.add(GUARD_OUT);
		return alphabet;
	}
}
