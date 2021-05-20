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
package multitrace.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import multitrace.DotMultiTraceRenderer;
import multitrace.MultiEvent;
import multitrace.MultiTraceElement;

/**
 * Unit tests for renderers.
 */
public class RendererTest
{
	@Test
	public void testDotRenderer1()
	{
		DotMultiTraceRenderer renderer = new DotMultiTraceRenderer();
		List<MultiTraceElement> trace = new ArrayList<MultiTraceElement>();
		trace.add(new MultiTraceElement(new MultiEvent("a")));
		trace.add(new MultiTraceElement(new MultiEvent("b", "c")));
		trace.add(new MultiTraceElement(new MultiEvent("a", "b", "c"), new MultiEvent("d", "e")));
		renderer.render(trace, System.out);
	}
}
