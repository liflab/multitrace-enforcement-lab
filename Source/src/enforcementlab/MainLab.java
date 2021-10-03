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

import static enforcementlab.GateExperiment.CORRECTIVE_ACTIONS;
import static enforcementlab.GateExperiment.DELETED_EVENTS;
import static enforcementlab.GateExperiment.ENDPOINTS_SCORED;
import static enforcementlab.GateExperiment.ENFORCEMENT_SWITCHES;
import static enforcementlab.GateExperiment.EVENT_SOURCE;
import static enforcementlab.GateExperiment.INPUT_EVENTS;
import static enforcementlab.GateExperiment.INSERTED_EVENTS;
import static enforcementlab.GateExperiment.INTERVAL;
import static enforcementlab.GateExperiment.MEMORY;
import static enforcementlab.GateExperiment.OUTPUT_EVENTS;
import static enforcementlab.GateExperiment.POLICY;
import static enforcementlab.GateExperiment.PROXY;
import static enforcementlab.GateExperiment.SCORING_FORMULA;
import static enforcementlab.GateExperiment.TIME;
import static enforcementlab.GateExperiment.TIME_PER_EVENT;
import static enforcementlab.GateExperiment.TRACE_SCORE;

import java.util.List;

import ca.uqac.lif.cep.enforcement.proxy.DeleteAny;
import ca.uqac.lif.cep.enforcement.proxy.InsertAny;
import ca.uqac.lif.cep.enforcement.selector.CountModifications;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.LatexNamer;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.server.WebCallback;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import enforcementlab.GateExperimentFactory.ScenarioRegion;
import enforcementlab.abc.AbcSource;
import enforcementlab.abc.DeleteAnyA;
import enforcementlab.abc.InsertAnyA;
import enforcementlab.abc.Property1;
import enforcementlab.abc.Property2;
import enforcementlab.abc.Property3;
import enforcementlab.file.AllFilesLifecycle;
import enforcementlab.file.FileSource;
import enforcementlab.museum.MinimizeIdleGuards;
import enforcementlab.museum.MuseumPolicy;
import enforcementlab.museum.MuseumProxy;
import enforcementlab.museum.MuseumSource;

@SuppressWarnings("unused")
public class MainLab extends Laboratory
{

	@Override
	public void setup()
	{
		setTitle("A benchmark for multi-trace enforcement pipelines");
		setAuthor("Rania Taleb, Sylvain Hallé and Raphaël Khoury");

		// Some random generators
		RandomBoolean r_bool = new RandomBoolean();
		r_bool.setSeed(getRandomSeed());
		RandomFloat r_float = new RandomFloat();
		r_float.setSeed(getRandomSeed());

		// The factory that generates the experiments
		TraceProvider p_trace = new TraceProvider(r_bool, r_float);
		ScoringProcessorProvider p_score = new ScoringProcessorProvider();
		ProxyProvider p_proxy = new ProxyProvider(p_trace);
		PolicyProvider p_policy = new PolicyProvider();
		GateExperimentFactory factory = new GateExperimentFactory(this, p_policy, p_proxy, p_trace, p_score);

		// General behavior
		{
			Group g = new Group("General behavior");
			g.setDescription("General measurements about the enforcement pipeline: execution time, number of corrective actions, etc.");
			add(g);
			ScenarioRegion big_r = new ScenarioRegion();
			big_r.add(EVENT_SOURCE, AbcSource.NAME, FileSource.NAME, MuseumSource.NAME);
			big_r.add(POLICY, Property1.NAME, Property2.NAME, Property3.NAME, AllFilesLifecycle.NAME, MuseumPolicy.NAME);
			big_r.add(PROXY, InsertAny.NAME, DeleteAny.NAME, MuseumProxy.NAME);
			big_r.add(SCORING_FORMULA, CountModifications.NAME, MinimizeIdleGuards.NAME);
			big_r.add(INTERVAL, 2, 4, 8);
			for (Region in_r : big_r.all(EVENT_SOURCE, POLICY, PROXY, SCORING_FORMULA))
			{
				String policy = in_r.getString(POLICY);
				String proxy = in_r.getString(PROXY);
				String scoring = in_r.getString(SCORING_FORMULA);
				String subtitle = "policy: " + policy + ", proxy: " + proxy + ", ranking: " + scoring;
				ExperimentTable et_ca = new ExperimentTable(INTERVAL, CORRECTIVE_ACTIONS, ENFORCEMENT_SWITCHES);
				et_ca.setTitle("Corrective actions depending on interval (" + subtitle + ")");
				add(et_ca);
				for (Region c_r : in_r.all(INTERVAL))
				{
					String subsubtitle = subtitle +  ", interval: " + c_r.getInt(INTERVAL);
					GateExperiment exp = factory.get(c_r);
					if (exp == null)
					{
						continue;
					}
					g.add(exp);
					{
						ExperimentTable et = new ExperimentTable(INPUT_EVENTS, OUTPUT_EVENTS, INSERTED_EVENTS, DELETED_EVENTS);
						et.setTitle("Input vs output events (" + subsubtitle + ")");
						et.add(exp);
						add(et);
						Scatterplot plot = new Scatterplot(et);
						plot.setCaption(Axis.X, "Input event index").setCaption(Axis.Y, "Output events");
						plot.withPoints(false);
						plot.setTitle(et.getTitle());
						add(plot);
					}
					{
						ExperimentTable et = new ExperimentTable(INPUT_EVENTS, TRACE_SCORE);
						et.setTitle("Evolution of trace score (" + subsubtitle + ")");
						et.add(exp);
						add(et);
						Scatterplot plot = new Scatterplot(et);
						plot.setCaption(Axis.X, "Input event index").setCaption(Axis.Y, "Score");
						plot.withPoints(false);
						plot.setTitle(et.getTitle());
						add(plot);
					}
					{
						ExperimentTable et = new ExperimentTable(INPUT_EVENTS, ENDPOINTS_SCORED);
						et.setTitle("Number of endpoints scored (" + subsubtitle + ")");
						et.add(exp);
						add(et);
						Scatterplot plot = new Scatterplot(et);
						plot.setCaption(Axis.X, "Input event index").setCaption(Axis.Y, "Endpoints scored");
						plot.withPoints(false);
						plot.setTitle(et.getTitle());
						add(plot);
					}
					{
						ExperimentTable et = new ExperimentTable(INPUT_EVENTS, MEMORY);
						et.setTitle("Memory consumption (" + subsubtitle + ")");
						et.add(exp);
						add(et);
						Scatterplot plot = new Scatterplot(et);
						plot.setCaption(Axis.X, "Input event index").setCaption(Axis.Y, "Memory (B)");
						plot.withPoints(false);
						plot.setTitle(et.getTitle());
						add(plot);
						et_ca.add(exp);
					}
					{
						ExperimentTable et = new ExperimentTable(INPUT_EVENTS, TIME_PER_EVENT);
						et.setTitle("Time per event (" + subsubtitle + ")");
						et.add(exp);
						add(et);
						Scatterplot plot = new Scatterplot(et);
						plot.setCaption(Axis.X, "Input event index").setCaption(Axis.Y, "Time per event (ms)");
						plot.withPoints(false);
						plot.setTitle(et.getTitle());
						add(plot);
						et_ca.add(exp);
					}
				}
			}
		}
		
		// Lab stats
		add(new LabStats(this));

		// Impact of proxy precision
		/*{
			Group g = new Group("Impact of proxy precision");
			g.setDescription("General measurements about the enforcement pipeline: execution time, number of corrective actions, etc.");
			add(g);
			setupComparisonProxy(factory, g, Property1.NAME, DeleteAny.NAME, DeleteAnyA.NAME);
			setupComparisonProxy(factory, g, Property1.NAME, InsertAny.NAME, InsertAnyA.NAME);
		}*/

		// Comparing the impact of interval length on time for brute-force vs. prefix tree
		/*{
			Region r = new Region();
			r.add(EVENT_SOURCE, SE_CASINO_RANDOM);
			r.add(SCORING_FORMULA, SC_MAXIMIZE_BETS, SC_MAXIMIZE_GAINS);
			r.add(INTERVAL, 1, 100, 500, 2000);
			r.add(TECHNIQUE, T_BRUTE_FORCE, T_PREFIX_TREE);
			for (Region r_p : r.all(SCORING_FORMULA, EVENT_SOURCE))
			{
				String formula = r_p.getString(SCORING_FORMULA);
				ExperimentTable et_time = new ExperimentTable(INTERVAL, TECHNIQUE, TIME);
				et_time.setShowInList(false);
				TransformedTable tt_time = new TransformedTable(new ExpandAsColumns(TECHNIQUE, TIME), et_time);
				tt_time.setNickname("tTimeBrutePrefixInterval" + LatexNamer.latexify(formula));
				tt_time.setTitle("Impact of interval on running time, brute force vs. prefix tree, scoring formula " + formula);
				add(et_time, tt_time);
				for (Region r_i : r_p.all(INTERVAL, TECHNIQUE))
				{
					MultiTraceSelectorExperiment e = factory.get(r_i);
					if (r_i == null)
					{
						continue;
					}
					et_time.add(e);
				}
			}
		}*/
	}
	
	/**
	 * Sets up experiments, tables and plots that compare the action of two
	 * proxies on the same policy. 
	 * @param factory The factory used to obtain experiment instances
	 * @param g The group to which experiments are to be added
	 * @param policy The name of the policy to enforce
	 * @param proxy1 The name of the first proxy
	 * @param proxy2 The name of the second proxy
	 */
	protected void setupComparisonProxy(GateExperimentFactory factory, Group g, String policy, String proxy1, String proxy2)
	{
		Region big_r = new Region();
		big_r.add(EVENT_SOURCE, AbcSource.NAME);
		big_r.add(POLICY, policy);
		big_r.add(PROXY, proxy1, proxy2);
		big_r.add(SCORING_FORMULA, CountModifications.NAME);
		big_r.add(INTERVAL, 8);
		for (Region in_r : big_r.all(EVENT_SOURCE, POLICY, SCORING_FORMULA, INTERVAL))
		{
			ExperimentTable et_events = new ExperimentTable(INPUT_EVENTS, PROXY, TIME_PER_EVENT);
			{
				et_events.setShowInList(false);
				TransformedTable tt = new TransformedTable(new ExpandAsColumns(PROXY, TIME_PER_EVENT), et_events);
				tt.setTitle("Impact of proxy precision on time (policy: " + policy + ")");
				add(et_events, tt);
				Scatterplot plot = new Scatterplot(tt);
				plot.setTitle(tt.getTitle());
				plot.setCaption(Axis.X, "Input event index").setCaption(Axis.Y, "Time per event (ms)");
				add(plot);
			}
			ExperimentTable et_memory = new ExperimentTable(INPUT_EVENTS, PROXY, MEMORY);
			{
				et_events.setShowInList(false);
				TransformedTable tt = new TransformedTable(new ExpandAsColumns(PROXY, MEMORY), et_memory);
				tt.setTitle("Impact of proxy precision on memory (policy: " + policy + ")");
				add(et_events, tt);
				Scatterplot plot = new Scatterplot(tt);
				plot.setTitle(tt.getTitle());
				plot.setCaption(Axis.X, "Input event index").setCaption(Axis.Y, "Memory (B)");
				add(plot);
			}
			for (Region p_r : in_r.all(PROXY))
			{
				GateExperiment exp = factory.get(p_r);
				if (exp == null)
				{
					continue;
				}
				g.add(exp);
				et_events.add(exp);
				et_memory.add(exp);
			}
		}
	}
	
	@Override
	public void setupCallbacks(List<WebCallback> callbacks)
	{
		callbacks.add(new PipelineStepCallback(this));
	}

	public static void main(String[] args)
	{
		MainLab.initialize(args, MainLab.class);
	}

}
