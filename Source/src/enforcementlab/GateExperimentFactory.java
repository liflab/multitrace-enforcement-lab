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
import ca.uqac.lif.cep.enforcement.proxy.DeleteAny;
import ca.uqac.lif.cep.enforcement.proxy.InsertAny;
import ca.uqac.lif.cep.enforcement.selector.CountModifications;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.labpal.ExperimentFactory;
import ca.uqac.lif.labpal.Region;
import enforcementlab.abc.AbcSource;
import enforcementlab.abc.DeleteAnyA;
import enforcementlab.abc.InsertAnyA;
import enforcementlab.abc.Property1;
import enforcementlab.abc.Property2;
import enforcementlab.abc.Property3;
import enforcementlab.casino.CasinoPolicy;
import enforcementlab.casino.CasinoProxy;
import enforcementlab.casino.CasinoSource;
import enforcementlab.casino.MaximizeBets;
import enforcementlab.casino.MaximizeGains;
import enforcementlab.casino.MaximizeGames;
import enforcementlab.file.AllFilesLifecycle;
import enforcementlab.file.FileSource;
import enforcementlab.museum.MinimizeIdleGuards;
import enforcementlab.museum.MuseumPolicy;
import enforcementlab.museum.MuseumProxy;
import enforcementlab.museum.MuseumSource;

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
	public GateExperiment get(Region r)
	{
		GateExperiment ge = super.get(r);
		if (ge != null)
		{
			ge.tellId(ge.getId());
		}
		return ge;
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
		ge.setInput(INTERVAL, interval);
		ge.setSource(src);
		ge.setPolicy(mon, r.getString(POLICY));
		ge.setProxy(prox, r.getString(PROXY));
		ge.setFilter(new IntervalFilter(mon, interval));
		ge.setSelector(mts, r.getString(SCORING_FORMULA));
		return ge;
	}

	/**
	 * A region that includes only combinations of source/policy/proxy/scoring
	 * that correspond to a valid scenario.
	 */
	public static class ScenarioRegion extends Region
	{
		public ScenarioRegion()
		{
			super();
		}
		
		public ScenarioRegion(Region r)
		{
			super(r);
		}
		
		@Override
		public boolean isInRegion(Region point)
		{
			if (!isFixed(point, EVENT_SOURCE))
			{
				return true;
			}
			String source = point.getString(EVENT_SOURCE);
			switch (source)
			{
			case AbcSource.NAME:
				if (isFixed(point, POLICY) && !oneOf(point.getString(POLICY), Property1.NAME, Property2.NAME, Property3.NAME))
				{
					return false;
				}
				if (isFixed(point, PROXY) && !oneOf(point.getString(PROXY), InsertAny.NAME, DeleteAny.NAME, InsertAnyA.NAME, DeleteAnyA.NAME))
				{
					return false;
				}
				if (isFixed(point, SCORING_FORMULA) && !oneOf(point.getString(SCORING_FORMULA), CountModifications.NAME))
				{
					return false;
				}
				break;
			case FileSource.NAME:
				if (isFixed(point, POLICY) && !oneOf(point.getString(POLICY), AllFilesLifecycle.NAME))
				{
					return false;
				}
				if (isFixed(point, PROXY) && !oneOf(point.getString(PROXY), DeleteAny.NAME))
				{
					return false;
				}
				if (isFixed(point, SCORING_FORMULA) && !oneOf(point.getString(SCORING_FORMULA), CountModifications.NAME))
				{
					return false;
				}
				break;
			case MuseumSource.NAME:
				if (isFixed(point, POLICY) && !oneOf(point.getString(POLICY), MuseumPolicy.NAME))
				{
					return false;
				}
				if (isFixed(point, PROXY) && !oneOf(point.getString(PROXY), MuseumProxy.NAME))
				{
					return false;
				}
				if (isFixed(point, SCORING_FORMULA) && !oneOf(point.getString(SCORING_FORMULA), CountModifications.NAME, MinimizeIdleGuards.NAME))
				{
					return false;
				}
				break;
			case CasinoSource.NAME:
				if (isFixed(point, POLICY) && !oneOf(point.getString(POLICY), CasinoPolicy.NAME))
				{
					return false;
				}
				if (isFixed(point, PROXY) && !oneOf(point.getString(PROXY), CasinoProxy.NAME))
				{
					return false;
				}
				if (isFixed(point, SCORING_FORMULA) && !oneOf(point.getString(SCORING_FORMULA), CountModifications.NAME, MaximizeGains.NAME, MaximizeGames.NAME, MaximizeBets.NAME))
				{
					return false;
				}
				break;
			}
			return true;
		}
		
		@Override
		protected ScenarioRegion getEmptyRegion()
		{
			return new ScenarioRegion();
		}

		@Override
		protected ScenarioRegion getRegion(Region r)
		{
			return new ScenarioRegion(r);
		}
		
		protected static boolean isFixed(Region r, String dimension)
		{
			if (!r.hasDimension(dimension))
			{
				return false;
			}
			return r.getAll(dimension).size() == 1;
		}
		
		protected static boolean oneOf(String s, String ... options)
		{
			for (String op : options)
			{
				if (s.compareTo(op) == 0)
				{
					return true;
				}
			}
			return false;
		}
	}
}
