package multitrace;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.SynchronousProcessor;
import ca.uqac.lif.cep.tmf.QueueSink;

public class Gate extends SynchronousProcessor
{
	/**
	 * The monitor used in the pipeline.
	 */
	protected Processor m_mu;
	
	/**
	 * The processor turning the proxy's output into prefix tree elements.
	 */
	protected Proxy m_proxy;
	
	/**
	 * The processor filtering the sequence of prefix tree elements.
	 */
	protected Filter m_filter;
	
	/**
	 * The processor choosing the trace to output based on the values produced
	 * by the ranking processor.
	 */
	protected Selector m_selector;
	
	/**
	 * A sink to accumulate the outputs from the selector.
	 */
	protected QueueSink m_sink;
	
	/**
	 * An endpoint to evaluate the input trace by the monitor, in order to
	 * determine if it is valid.
	 */
	protected Endpoint<Event,Quadrilean.Value> m_endpoint;
	
	/**
	 * An instance of {@link Pushable} to send events to the enforcement
	 * pipeline.
	 */
	protected Pushable m_pushable;
	
	/**
	 * A prefix of events received and buffered by the gate.
	 */
	protected List<Event> m_prefix;
	
	public Gate(Processor mu, Proxy p, Filter f, Selector s)
	{
		super(2, 1);
		m_mu = mu;
		m_endpoint = new Endpoint<Event,Quadrilean.Value>(mu);
		m_proxy = p;
		m_filter = f;
		m_selector = s;
		m_sink = new QueueSink();
		m_pushable = m_proxy.getPushableInput();
		Connector.connect(m_proxy, m_filter);
		Connector.connect(m_filter, m_selector);
		Connector.connect(m_selector, m_sink);
		m_prefix = new ArrayList<Event>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		Event in_event = (Event) inputs[0];
		Quadrilean.Value verdict = m_endpoint.getVerdict(in_event);
		m_prefix.add(in_event);
		if (verdict == Quadrilean.Value.TRUE || verdict == Quadrilean.Value.P_TRUE)
		{
			// Property is satisfied: let the event through
			for (Event e : m_prefix)
			{
				outputs.add(new Object[] {e});
			}
			m_proxy.apply(m_prefix);
			m_filter.apply(m_prefix);
			m_selector.apply(m_prefix);
			return true;
		}
		// Wait for the enforcement pipeline to output something
		m_pushable.push(in_event);
		Queue<Object> q = m_sink.getQueue();
		if (!q.isEmpty())
		{
			// The pipeline produced a sequence: output it
			List<Event> to_output = (List<Event>) q.remove();
			for (Event out_event : to_output)
			{
				outputs.add(new Object[] {out_event});
			}
		}
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
