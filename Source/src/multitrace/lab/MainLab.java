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

import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.LatexNamer;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;

import static multitrace.lab.MultiTraceProvider.SE_CASINO_RANDOM;
import static multitrace.lab.MultiTraceSelectorExperiment.EVENT_SOURCE;
import static multitrace.lab.MultiTraceSelectorExperiment.INTERVAL;
import static multitrace.lab.MultiTraceSelectorExperiment.SCORING_FORMULA;
import static multitrace.lab.MultiTraceSelectorExperiment.TECHNIQUE;
import static multitrace.lab.MultiTraceSelectorExperiment.TIME;
import static multitrace.lab.MultiTraceSelectorExperimentFactory.T_BRUTE_FORCE;
import static multitrace.lab.MultiTraceSelectorExperimentFactory.T_PREFIX_TREE;
import static multitrace.lab.ScoringProcessorProvider.SC_MAXIMIZE_BETS;
import static multitrace.lab.ScoringProcessorProvider.SC_MAXIMIZE_GAINS;

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
		MultiTraceProvider p_trace = new MultiTraceProvider(r_bool, r_float);
		ScoringProcessorProvider p_score = new ScoringProcessorProvider();
		MultiTraceSelectorExperimentFactory factory = new MultiTraceSelectorExperimentFactory(this, p_trace, p_score);
		
		// A big region with the lab's parameters
		Region big_r = new Region();
		
		// Comparing the impact of interval length on time for brute-force vs. prefix tree
		{
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
		}
	}
	
	public static void main(String[] args)
	{
		MainLab.initialize(args, MainLab.class);
	}

}
