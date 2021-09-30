package enforcementlab;

import java.util.List;
import java.util.Map;

import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.server.TemplatePageCallback;

public class PipelineStepCallback extends TemplatePageCallback
{

	public PipelineStepCallback(Laboratory lab)
	{
		super("/trace", lab, null);
		m_filename = "resource/index.html";
	}
	
	/**
	 * Fills the content of the page.
	 * @param contents The string builder where the contents of the page will be printed
	 * @param params Any parameters fetched from the page's URL when it was called
	 * @param is_offline Set to <tt>true</tt> to generate the page for offline use
	 */
	protected void fill(StringBuilder contents, Map<String, String> params, boolean is_offline)
	{
		int e_id = Integer.parseInt(params.get("").trim());
		GateExperiment exp = (GateExperiment) m_lab.getExperiment(e_id);
		if (exp == null)
		{
			contents.append("Invalid experiment ID: " + e_id);
			return;
		}
		List<PipelineStep> steps = exp.getPipelineSteps();
		contents.append("<table border=\"1\">\n");
		for (int i = 0; i < steps.size(); i++)
		{
			PipelineStep step = steps.get(i);
			contents.append(step.toHtmlRow(i));
		}
		contents.append("</table>\n");
	}
	
	@Override
	public String fill(String s, Map<String, String> params, boolean is_offline)
	{
		StringBuilder contents = new StringBuilder();
		fill(contents, params, is_offline);
		s = s.replace("{%TITLE%}", "Pipeline steps");
		s = s.replace("{%LAB_DESCRIPTION%}", contents.toString());
		return s;
	}

}
