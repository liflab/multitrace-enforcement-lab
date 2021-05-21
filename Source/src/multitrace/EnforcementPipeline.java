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

import java.util.List;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;

/**
 * Pipeline that applies runtime enforcement. It is composed of a linear chain
 * comprising:
 * <ol>
 * <li>A {@link MultiTraceProxy}, which takes a trace of events and produces a
 * multi-trace</li>
 * <li>A {@link MultiTraceFilter}, which filters the multi-trace based on the
 * verdict produced by an underlying monitor</li>
 * <li>A {@link MonotonicMultiTraceSelector}, which periodically evaluates these
 * multi-traces against a scoring processor and outputs the uni-trace
 * projection with the highest score</li>
 * </ol>
 */
public class EnforcementPipeline extends GroupProcessor
{
	/**
	 * The proxy in the pipeline.
	 */
	protected MultiTraceProxy m_proxy;
	
	/**
	 * The processor filtering traces from the proxy.
	 */
	protected MultiTraceFilter m_filter;
	
	/**
	 * The processor selecting the highest-ranked uni-trace coming out of
	 * the filter.
	 */
	protected MultiTraceSelector m_selector; 
	 
	/**
	 * Creates a new instance of the enforcement pipeline.
	 * @param proxy The proxy producing multi-events
	 * @param monitor The monitor evaluating a property on uni-traces
	 * @param scorer The scoring processor producing an integer score on
	 * uni-traces
	 */
	public EnforcementPipeline(Processor proxy, Processor monitor, Processor scorer)
	{
		super(1, 1);
		m_proxy = new MultiTraceProxy(proxy);
		m_filter = new MultiTraceFilter(monitor);
		Connector.connect(m_proxy, m_filter);
		m_selector = new MultiTraceSelector(scorer);
		m_selector.setEnforcementPipeline(this);
		Connector.connect(m_filter, m_selector);
		addProcessors(m_proxy, m_filter, m_selector);
		associateInput(0, m_proxy, 0);
		associateOutput(0, m_selector, 0);
	}
	
	/**
	 * Receives a signal from the selector that a uni-trace of events has been
	 * produced. The pipeline uses it to notify the proxy and the filter of the
	 * events that have actually been chosen as the output uni-trace.
	 * @param trace The trace of uni-events
	 */
	public void apply(List<Event> trace)
	{
		m_proxy.restartTree();
		m_filter.apply(trace);
	}
}
