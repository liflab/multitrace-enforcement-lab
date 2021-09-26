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

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.Filter;
import ca.uqac.lif.cep.enforcement.Gate;
import ca.uqac.lif.cep.enforcement.Proxy;
import ca.uqac.lif.cep.enforcement.Selector;
import ca.uqac.lif.cep.tmf.QueueSink;
import ca.uqac.lif.cep.tmf.Source;
import ca.uqac.lif.json.JsonList;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.ExperimentException;

/**
 * Experiment that runs an enforcement pipeline on a source of events, and
 * measures various statistics about its execution.
 */
public class GateExperiment extends Experiment
{
	/**
	 * The name of parameter "event source".
	 */
	public static final transient String EVENT_SOURCE = "Event source";
	
	/**
	 * The name of parameter "policy".
	 */
	public static final transient String POLICY = "Policy";
	
	/**
	 * The name of parameter "proxy".
	 */
	public static final transient String PROXY = "Proxy";
	
	/**
	 * The name of parameter "scoring formula".
	 */
	public static final transient String SCORING_FORMULA = "Scoring formula";
	
	/**
	 * The name of parameter "input events".
	 */
	public static final transient String INPUT_EVENTS = "Input events";
	
	/**
	 * The name of parameter "output events".
	 */
	public static final transient String OUTPUT_EVENTS = "Output events";
	
	/**
	 * The name of parameter "interval".
	 */
	public static final transient String TIME = "Time";
	
	/**
	 * The name of parameter "interval".
	 */
	public static final transient String INTERVAL = "Interval";
	
	/**
	 * The source of events.
	 */
	protected transient Source m_source;
	
	/**
	 * The processor acting as the policy monitor in the pipeline.
	 */
	protected transient Processor m_monitor;
	
	/**
	 * The proxy used to create multi-traces in the pipeline.
	 */
	protected transient Proxy m_proxy;
	
	/**
	 * The processor used as the filter in the pipeline.
	 */
	protected transient Filter m_filter;
	
	/**
	 * The processor used as the selector in the pipeline.
	 */
	protected transient Selector m_selector;
	
	/**
	 * Creates a new empty enforcement pipeline experiment.
	 */
	public GateExperiment()
	{
		super();
		describe(POLICY, "The security policy being enforced");
		describe(PROXY, "The proxy applying corrective modifications");
		describe(SCORING_FORMULA, "The scoring formula used to rank traces");
		describe(INPUT_EVENTS, "The number of input events given to the pipeline");
		describe(OUTPUT_EVENTS, "The number of output events produced by the pipeline");
		describe(TIME, "The time (in milliseconds) taken by the selector to produce the output trace");
		write(INPUT_EVENTS, new JsonList());
		write(OUTPUT_EVENTS, new JsonList());
	}
	
	/**
	 * Sets the source of events in this experiment.
	 * @param p The source of events
	 * @return This experiment
	 */
	public GateExperiment setSource(Source p)
	{
		m_source = p;
		return this;
	}
	
	/**
	 * Sets the security policy to enforce in this experiment.
	 * @param p The processor acting as the policy monitor in the pipeline
	 * @param name A name given to this processor
	 * @return This experiment
	 */
	public GateExperiment setPolicy(Processor p, String name)
	{
		m_monitor = p;
		setInput(POLICY, name);
		return this;
	}
	
	/**
	 * Sets the proxy applying corrective modifications in this experiment.
	 * @param p The proxy used to create multi-traces in the pipeline
	 * @param name A name given to this processor
	 * @return This experiment
	 */
	public GateExperiment setProxy(Proxy p, String name)
	{
		m_proxy = p;
		setInput(PROXY, name);
		return this;
	}
	
	/**
	 * Sets the filter removing invalid traces in this experiment.
	 * @param p The processor used as the filter in the pipeline
	 * @return This experiment
	 */
	public GateExperiment setFilter(Filter p)
	{
		m_filter = p;
		return this;
	}
	
	/**
	 * Sets the scoring formula used to rank traces in this experiment.
	 * @param p The processor used as the selector in the pipeline
	 * @param name A name given to this processor
	 * @return This experiment
	 */
	public GateExperiment setSelector(Selector p, String name)
	{
		m_selector = p;
		setInput(SCORING_FORMULA, name);
		return this;
	}

	@Override
	public void execute() throws ExperimentException, InterruptedException
	{
		Gate g = new Gate(m_monitor, m_proxy, m_filter, m_selector);
		QueueSink sink = new QueueSink();
		Queue<?> queue = sink.getQueue();
		Connector.connect(g, sink);
		Pullable s_p = m_source.getPullableOutput();
		Pushable g_p = g.getPushableInput();
		int in_c = 0, out_c = 0;
		JsonList in_l = (JsonList) read(INPUT_EVENTS);
		JsonList out_l = (JsonList) read(OUTPUT_EVENTS);
		long start = System.currentTimeMillis();
		while (s_p.hasNext())
		{
			in_c++;
			Event e = (Event) s_p.next();
			g_p.push(e);
			out_c += queue.size();
			queue.remove();
			in_l.add(in_c);
			out_l.add(out_c);
		}
		long end = System.currentTimeMillis();
		write(TIME, end - start);
	}
}
