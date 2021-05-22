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

import ca.uqac.lif.cep.Processor;

/**
 * Endpoint for a processor producing Boolean values and acting as a runtime
 * monitor. This endpoint presents the optimization that as soon as the
 * monitor produces the false verdict, itself and its duplicates no longer
 * interact with the monitor upon a call to {@link #getVerdict(Event)} and
 * simply return false. 
 */
public class MonitorEndpoint extends Endpoint<Event,Boolean>
{
	/**
	 * Flag that determines if this endpoint corresponds to a trace
	 * prefix that already violates the monitor's property.
	 */
	protected boolean m_violated = false;
	
	public MonitorEndpoint(Processor monitor)
	{
		super(monitor);
		m_violated = false;
	}
	
	@Override
	public MonitorEndpoint duplicate()
	{
		if (!m_violated)
		{
			return new MonitorEndpoint(m_processor.duplicate(true));
		}
		// Don't bother duplicating if endpoint is a bad prefix
		return this;
	}
	
	@Override
	public Boolean getVerdict(Event e)
	{
		if (m_violated)
		{
			// Don't bother calling the monitor
			return false;
		}
		boolean b = (Boolean) super.getVerdict(e);
		if (!b)
		{
			m_violated = true;
		}
		return b;
	}
}
