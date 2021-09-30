package enforcementlab;

import java.util.List;

import ca.uqac.lif.cep.enforcement.Event;

public class PipelineStep
{
	public Event m_inputEvent;

	public List<Event> m_outputEvents;

	public PipelineStep(Event in_event, List<Event> out_events)
	{
		super();
		m_inputEvent = in_event;
		m_outputEvents = out_events;
	}

	public String toHtmlRow(int index)
	{
		StringBuilder out = new StringBuilder();
		String rowspan = "";
		int num_out = m_outputEvents.size();
		if (num_out == 0)
		{
			out.append("<tr><td>").append(index).append("</td><td>").append(m_inputEvent).append("</tr><td></td></tr>\n");
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
