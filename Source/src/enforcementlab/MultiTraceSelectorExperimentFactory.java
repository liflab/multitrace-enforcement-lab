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

import static enforcementlab.MultiTraceSelectorExperiment.EVENT_SOURCE;
import static enforcementlab.MultiTraceSelectorExperiment.INTERVAL;
import static enforcementlab.MultiTraceSelectorExperiment.SCORING_FORMULA;
import static enforcementlab.MultiTraceSelectorExperiment.TECHNIQUE;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.enforcement.BruteForceMultiTraceSelector;
import ca.uqac.lif.cep.enforcement.IntervalSelector;
import ca.uqac.lif.cep.enforcement.MultiTraceSelector;
import ca.uqac.lif.cep.enforcement.PrefixTreeMultiTraceSelector;
import ca.uqac.lif.cep.enforcement.Selector;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Region;

/**
 * Produces multi-trace selector experiments based on the contents of a region.
 */
public class MultiTraceSelectorExperimentFactory extends ExperimentFactory<MainLab,MultiTraceSelectorExperiment>
{
	/**
	 * Name of technique "brute force".
	 */
	public static final transient String T_BRUTE_FORCE = "Brute force";
	
	/**
	 * Name of technique "prefix tree".
	 */
	public static final transient String T_PREFIX_TREE = "Prefix tree";
	
	/**
	 * A provider producing event sources from a region.
	 */
	protected TraceProvider m_traceProvider;
	
	/**
	 * A provider producing scoring processors from a region.
	 */
	protected ScoringProcessorProvider m_scoreProvider;
	
	/**
	 * Creates a new instance of the factory.
	 * @param lab The lab where the experiments are to be added
	 * @param traces A provider producing event sources from a region
	 * @param scores A provider producing scoring processors from a region
	 */
	public MultiTraceSelectorExperimentFactory(MainLab lab, TraceProvider traces, ScoringProcessorProvider scores)
	{
		super(lab, MultiTraceSelectorExperiment.class);
		m_traceProvider = traces;
		m_scoreProvider = scores;
	}

	@Override
	protected MultiTraceSelectorExperiment createExperiment(Region r)
	{
		Source src = m_traceProvider.get(r);
		Processor score = m_scoreProvider.get(r);
		if (src == null || score == null)
		{
			return null;
		}
		int interval = 1;
		if (r.hasDimension(INTERVAL))
		{
			interval = r.getInt(INTERVAL);
		}
		Selector mts = null;
		String technique = r.getString(TECHNIQUE);
		if (technique.compareTo(T_BRUTE_FORCE) == 0)
		{
			mts = new BruteForceMultiTraceSelector(score);
		}
		if (technique.compareTo(T_PREFIX_TREE) == 0)
		{
			//mts = new PrefixTreeMultiTraceSelector(score);
			mts = new IntervalSelector(score, interval);
		}
		if (mts == null)
		{
			return null;
		}
		MultiTraceSelectorExperiment mtse = new MultiTraceSelectorExperiment(src, mts, interval);
		mtse.setInput(TECHNIQUE, technique);
		mtse.setInput(SCORING_FORMULA, r.get(SCORING_FORMULA));
		mtse.setInput(EVENT_SOURCE, r.get(EVENT_SOURCE));
		return mtse;
	}

}
