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

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.size.SizePrinter;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.Pushable.PushableException;
import ca.uqac.lif.cep.enforcement.CannotFixException;
import ca.uqac.lif.cep.enforcement.Checkpointable;
import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.Event.Added;
import ca.uqac.lif.cep.enforcement.Event.Deleted;
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
	 * The name of parameter "inserted events".
	 */
	public static final transient String INSERTED_EVENTS = "Inserted events";

	/**
	 * The name of parameter "deleted events".
	 */
	public static final transient String DELETED_EVENTS = "Deleted events";

	/**
	 * The name of parameter "corrective actions".
	 */
	public static final transient String CORRECTIVE_ACTIONS = "Corrective actions";

	/**
	 * The name of parameter "time".
	 */
	public static final transient String TIME = "Time";

	/**
	 * The name of parameter "time per event".
	 */
	public static final transient String TIME_PER_EVENT = "Time per event";

	/**
	 * The name of parameter "interval".
	 */
	public static final transient String INTERVAL = "Interval";

	/**
	 * The name of parameter "memory".
	 */
	public static final transient String MEMORY = "Memory";

	/**
	 * The name of parameter "throughput".
	 */
	public static final transient String THROUGHPUT = "Throughput";

	/**
	 * The name of parameter "enforcement switches".
	 */
	public static final transient String ENFORCEMENT_SWITCHES = "Enforcement switches";

	/**
	 * The name of parameter "endpoints scored".
	 */
	public static final transient String ENDPOINTS_SCORED = "Endpoints scored";

	/**
	 * The name of parameter "trace score".
	 */
	public static final transient String TRACE_SCORE = "Trace score";

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
	 * The list of pipeline steps as recorded by the experiment.
	 */
	protected transient List<PipelineStep> m_pipelineSteps;

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
		describe(INSERTED_EVENTS, "The number of events inserted by the pipeline");
		describe(DELETED_EVENTS, "The number of events deleted by the pipeline");
		describe(OUTPUT_EVENTS, "The number of output events produced by the pipeline");
		describe(TIME, "The time (in milliseconds) taken by the selector to produce the output trace");
		describe(ENDPOINTS_SCORED, "The cumulative number of trace segments that have been ranked");
		describe(TRACE_SCORE, "The score given to the current trace");
		describe(TIME_PER_EVENT, "The time (in milliseconds) taken by the selector to process each input event");
		describe(MEMORY, "The memory consumed by the enforcement pipeline");
		describe(THROUGHPUT, "The average number of events per second ingested by the enforcement pipeline");
		describe(CORRECTIVE_ACTIONS, "The total number of events that have been added or deleted");
		write(INPUT_EVENTS, new JsonList());
		write(OUTPUT_EVENTS, new JsonList());
		write(INSERTED_EVENTS, new JsonList());
		write(DELETED_EVENTS, new JsonList());
		write(MEMORY, new JsonList());
		write(TIME_PER_EVENT, new JsonList());
		write(TRACE_SCORE, new JsonList());
		write(ENDPOINTS_SCORED, new JsonList());
		m_pipelineSteps = new ArrayList<PipelineStep>();
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
		m_source.reset();
		if (m_source instanceof Checkpointable)
		{
			g.notify((Checkpointable) m_source);
		}
		Pullable s_p = m_source.getPullableOutput();
		Pushable g_p = g.getPushableInput();
		int in_c = 0, out_c = 0, ins_c = 0, del_c = 0;
		JsonList in_l = (JsonList) read(INPUT_EVENTS);
		JsonList out_l = (JsonList) read(OUTPUT_EVENTS);
		JsonList ins_l = (JsonList) read(INSERTED_EVENTS);
		JsonList del_l = (JsonList) read(DELETED_EVENTS);
		JsonList mem_l = (JsonList) read(MEMORY);
		JsonList tpe_l = (JsonList) read(TIME_PER_EVENT);
		JsonList eps_l = (JsonList) read(ENDPOINTS_SCORED);
		JsonList sco_l = (JsonList) read(TRACE_SCORE);
		SizePrinter sp = new SizePrinter();
		sp.ignoreAccessChecks(true);
		long start = System.currentTimeMillis();
		long lap = start;
		while (s_p.hasNext())
		{
			in_c++;
			Event e = (Event) s_p.next();
			try
			{
				g_p.push(e);
			}
			catch (PushableException pe)
			{
				if (pe.getCause() instanceof CannotFixException)
				{
					m_pipelineSteps.add(new PipelineStep(e, null));
					// Exhaust the input event source
					while (s_p.hasNext())
					{
						e = (Event) s_p.next();
						m_pipelineSteps.add(new PipelineStep(e, null));
					}
				}
				throw new ExperimentException(pe);
			}
			out_c += queue.size();
			int mem = 0;
			List<Event> e_output = new ArrayList<Event>();
			while (!queue.isEmpty())
			{
				Event q_e = (Event) queue.remove();
				e_output.add(q_e);
				if (q_e instanceof Added)
				{
					ins_c++;
				}
				if (q_e instanceof Deleted)
				{
					del_c++;
					out_c--;
				}
			}
			m_pipelineSteps.add(new PipelineStep(e, e_output));
			sp.reset();
			try
			{
				mem = sp.print(m_filter.getElements()).intValue();
			}
			catch (PrintException e1)
			{
				throw new ExperimentException(e1);
			}
			long now = System.currentTimeMillis();
			tpe_l.add(now - lap);
			lap = now;
			in_l.add(in_c);
			out_l.add(out_c);
			ins_l.add(ins_c);
			del_l.add(del_c);
			mem_l.add(mem);
			eps_l.add(m_selector.getEndpointsScored());
			sco_l.add(m_selector.getScore());
		}
		long end = System.currentTimeMillis();
		write(TIME, end - start);
		write(THROUGHPUT,  in_c * 100 / (end - start));
		write(CORRECTIVE_ACTIONS, ins_c + del_c);
		write(ENFORCEMENT_SWITCHES, g.getEnforcementSwitches());
	}

	/**
	 * Called by the factory to notify the experiment that an ID has been
	 * assigned to it. This method circumvents the fact that an experiment does
	 * not yet know its ID when its constructor is called.
	 * @param id The experiment's id
	 */
	public void tellId(int id)
	{
		writeDescription();
	}

	/**
	 * Writes the description of the experiment.
	 */
	protected void writeDescription()
	{
		StringBuilder out = new StringBuilder();
		out.append("<p>Runs an enforcement pipeline on a trace of events.</p>\n");
		out.append("<p><a href=\"/trace/").append(getId()).append("\">See the pipeline steps</a></p>\n");
		setDescription(out.toString());
	}

	public List<PipelineStep> getPipelineSteps()
	{
		return m_pipelineSteps;
	}
}
