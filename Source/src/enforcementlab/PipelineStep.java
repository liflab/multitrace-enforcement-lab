package enforcementlab;

import java.util.List;

import ca.uqac.lif.cep.enforcement.Event;

public class PipelineStep
{
	/*@ non_null @*/ public Event m_inputEvent;

	/*@ null @*/ public List<Event> m_outputEvents;

	public PipelineStep(/*@ non_null @*/ Event in_event, /*@ null @*/ List<Event> out_events)
	{
		super();
		m_inputEvent = in_event;
		m_outputEvents = out_events;
	}

	public String toHtmlRow(int index)
	{
		StringBuilder out = new StringBuilder();
		String rowspan = "";
		int num_out = 0;
		if (m_outputEvents != null)
		{
			num_out = m_outputEvents.size();
		}
		if (num_out == 0)
		{
			
			out.append("<tr><td>").append(index).append("</td><td>").append(m_inputEvent).append("</td><td>");
			if (m_outputEvents == null)
			{
				out.append("X");
			}
			out.append("</td></tr>\n");
		}
		else
		{
			if (num_out > 1)
			{
				rowspan = " rowspan=\"" + m_outputEvents.size() + "\"";
			}
			out.append("<tr><td").append(rowspan).append(">").append(index).append("</td><td").append(rowspan).append(">").append(m_inputEvent).append("</td>");
			for (int i = 0; i < num_out; i++)
			{
				if (i > 0)
				{
					out.append("</tr>\n<tr>");
				}
				out.append("<td>").append(m_outputEvents.get(i)).append("</td>");
			}
			out.append("</tr>\n");
		}
		return out.toString();
	}
}
