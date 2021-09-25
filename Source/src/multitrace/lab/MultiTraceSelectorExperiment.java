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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.BlackHole;
import ca.uqac.lif.cep.tmf.Passthrough;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.ExperimentException;
import multitrace.AppendToMultiTrace;
import multitrace.PrefixTreeElement;
import multitrace.Proxy;
import multitrace.Selector;
import multitrace.WrapEvent;

/**
 * Experiment that runs a trace of multi-events through a multi-trace
 * selector, and measures its resulting score and running time. Note that this
 * experiment focuses on the behavior of the selector, and not the whole
 * enforcement pipeline.
 */
public class MultiTraceSelectorExperiment extends Experiment
{
	/**
	 * The name of parameter "interval".
	 */
	public static final transient String INTERVAL = "Interval";
	
	/**
	 * The name of parameter "score".
	 */
	public static final transient String SCORE = "Score";
	
	/**
	 * The name of parameter "event source".
	 */
	public static final transient String EVENT_SOURCE = "Event source";
	
	/**
	 * The name of parameter "scoring formula".
	 */
	public static final transient String SCORING_FORMULA = "Scoring formula";
	
	/**
	 * The name of parameter "technique".
	 */
	public static final transient String TECHNIQUE = "Technique";
	
	/**
	 * The name of parameter "interval".
	 */
	public static final transient String TIME = "Time";
	
	/**
	 * The source of events.
	 */
	protected transient Source m_source;
	
	/**
	 * The multi-trace selector to evaluate.
	 */
	protected transient Selector m_selector;
	
	public MultiTraceSelectorExperiment(Source event_source, Selector mts, int interval)
	{
		super();
		describe(INTERVAL, "The number of buffered input events between each output of the selector");
		describe(EVENT_SOURCE, "The source of multi-events in this experiment");
		describe(SCORING_FORMULA, "The scoring formula used to rank traces");
		describe(TECHNIQUE, "The algorithm used to select output events");
		describe(TIME, "The time (in milliseconds) taken by the selector to produce the output trace");
		describe(SCORE, "The score obtained by the uni-trace produced as output by the selector");
		setInput(INTERVAL, interval);
		m_source = event_source;
		m_selector = mts;
	}

	@Override
	public void execute() throws ExperimentException, InterruptedException
	{
		BlackHole hole = new BlackHole();
		Connector.connect(m_selector, hole);
		Pushable s_p = m_selector.getPushableInput();
		Proxy atm = new Proxy(new Passthrough());
		//AppendToMultiTrace atm = new AppendToMultiTrace();
		Connector.connect(m_source, atm);
		Pullable q_p = atm.getPullableOutput();
		long start = System.currentTimeMillis();
		int num = 0, interval = readInt(INTERVAL);
		while (q_p.hasNext())
		{
			if (num % interval == 0)
			{
				atm.reset();
			}
			PrefixTreeElement mte = (PrefixTreeElement) q_p.pull();
			s_p.push(mte);
		}
		long end = System.currentTimeMillis();
		write(SCORE, m_selector.getScore());
		write(TIME, end - start);
	}
}
