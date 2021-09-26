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
package enforcementlab;

import static enforcementlab.MultiTraceSelectorExperiment.SCORING_FORMULA;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.labpal.Region;
import enforcementlab.casino.MaximizeBets;
import enforcementlab.casino.MaximizeGains;

public class ScoringProcessorProvider
{
	/**
	 * The "maximize gains" scoring formula in the casino scenario.
	 */
	public static final transient String SC_MAXIMIZE_GAINS = "Maximize gains";
	
	/**
	 * The "maximize bets" scoring formula in the casino scenario.
	 */
	public static final transient String SC_MAXIMIZE_BETS = "Maximize bets";
	
	/**
	 * Gets a scoring processor.
	 * @param r The region containing parameters to select the processor.
	 * @return The processor, or <tt>null</tt> if no processor could be
	 * produced for the given region.
	 */
	public Processor get(Region r)
	{
		String property = r.getString(SCORING_FORMULA);
		if (property.compareTo(SC_MAXIMIZE_GAINS) == 0)
		{
			return new MaximizeGains();
		}
		if (property.compareTo(SC_MAXIMIZE_BETS) == 0)
		{
			return new MaximizeBets();
		}
		return null;
	}
}
