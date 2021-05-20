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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders a multi-trace as a Graphviz file that displays it as a tree.
 */
public class DotMultiTraceRenderer implements MultiTraceRenderer
{
	@Override
	public void render(List<MultiTraceElement> trace, PrintStream ps)
	{
		int id_counter = 0;
		ps.println("digraph G {");
		ps.println(" node [shape=\"circle\"];");
		ps.println(" 0 [label=\"\"];");
		List<Integer> parents = new ArrayList<Integer>();
		parents.add(0);
		for (MultiTraceElement mte : trace)
		{
			List<Integer> new_parents = new ArrayList<Integer>();
			for (int i = 0; i < parents.size(); i++)
			{
				int parent = parents.get(i);
				MultiEvent me = mte.get(i);
				for (Event e : me)
				{
					int id = ++id_counter;
					ps.println(" " + id + " [label=\"" + e + "\"];");
					ps.println(" " + parent + " -> " + id + ";");
					new_parents.add(id);
				}
			}
			parents = new_parents;
		}
		ps.println("}");
	}
}
