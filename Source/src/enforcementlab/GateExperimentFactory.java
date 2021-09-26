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

import static enforcementlab.GateExperiment.EVENT_SOURCE;
import static enforcementlab.GateExperiment.INTERVAL;
import static enforcementlab.GateExperiment.POLICY;
import static enforcementlab.GateExperiment.PROXY;
import static enforcementlab.GateExperiment.SCORING_FORMULA;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.enforcement.IntervalFilter;
import ca.uqac.lif.cep.enforcement.IntervalSelector;
import ca.uqac.lif.cep.enforcement.Proxy;
import ca.uqac.lif.cep.enforcement.Selector;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Region;

/**
 * Produces gate experiments based on the contents of a region.
 */
public class GateExperimentFactory extends ExperimentFactory<MainLab,GateExperiment>
{
	/**
	 * A provider producing event sources from a region.
	 */
	protected TraceProvider m_traceProvider;
	
	/**
	 * A provider producing monitors from a region.
	 */
	protected PolicyProvider m_policyProvider;
	
	/**
	 * A provider producing proxies from a region.
	 */
	protected ProxyProvider m_proxyProvider;
	
	/**
	 * A provider producing scoring processors from a region.
	 */
	protected ScoringProcessorProvider m_scoreProvider;
	
	/**
	 * Creates a new instance of the factory.
	 * @param lab The lab where the experiments are to be added
	 * @param monitors A provider producing monitors from a region
	 * @param proxies A provider producing monitors from a region
	 * @param traces A provider producing event sources from a region
	 * @param scores A provider producing scoring processors from a region
	 */
	public GateExperimentFactory(MainLab lab, PolicyProvider monitors, ProxyProvider proxies, TraceProvider traces, ScoringProcessorProvider scores)
	{
		super(lab, GateExperiment.class);
		m_traceProvider = traces;
		m_scoreProvider = scores;
		m_policyProvider = monitors;
		m_proxyProvider = proxies;
	}

	@Override
	protected GateExperiment createExperiment(Region r)
	{
		Source src = m_traceProvider.get(r);
		Processor score = m_scoreProvider.get(r);
		Processor mon = m_policyProvider.get(r);
		Proxy prox = m_proxyProvider.get(r);
		if (src == null || score == null || mon == null)
		{
			return null;
		}
		int interval = 1;
		if (r.hasDimension(INTERVAL))
		{
			interval = r.getInt(INTERVAL);
		}
		Selector mts = new IntervalSelector(score, interval);
		GateExperiment ge = new GateExperiment();
		ge.setSource(src);
		ge.setPolicy(mon, r.getString(POLICY));
		ge.setProxy(prox, r.getString(PROXY));
		ge.setFilter(new IntervalFilter(mon, interval));
		ge.setSelector(mts, r.getString(SCORING_FORMULA));
		return ge;
	}
}
