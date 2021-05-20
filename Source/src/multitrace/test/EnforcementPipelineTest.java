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

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.tmf.SinkLast;
import multitrace.EnforcementPipeline;
import multitrace.Event;
import multitrace.MultiEvent;
import multitrace.MultiTraceSelector;
import multitrace.StateMooreMachine;
import multitrace.StateMooreMachine.EventTransition;

/**
 * Unit tests for the {@link MultiTraceSelector} processor.
 */
public class EnforcementPipelineTest
{
	protected static final Event a = Event.get("a");
	protected static final Event b = Event.get("b");
	protected static final Event c = Event.get("c");
	
	@Test
	public void testHighestString1()
	{
		StateMooreMachine invert_b_c = new StateMooreMachine(1, 1);
		{
			invert_b_c.addTransition(0, new EventTransition(a, 1));
			invert_b_c.addTransition(0, new EventTransition(b, 2));
			invert_b_c.addTransition(0, new EventTransition(c, 3));
			invert_b_c.addTransition(1, new EventTransition(a, 1));
			invert_b_c.addTransition(2, new EventTransition(a, 1));
			invert_b_c.addTransition(3, new EventTransition(a, 1));
			invert_b_c.addTransition(1, new EventTransition(b, 2));
			invert_b_c.addTransition(2, new EventTransition(b, 2));
			invert_b_c.addTransition(3, new EventTransition(b, 2));
			invert_b_c.addTransition(1, new EventTransition(c, 3));
			invert_b_c.addTransition(2, new EventTransition(c, 3));
			invert_b_c.addTransition(3, new EventTransition(c, 3));
			invert_b_c.addSymbol(0, new Constant(new MultiEvent(Event.EPSILON)));
			invert_b_c.addSymbol(1, new Constant(new MultiEvent(a)));
			invert_b_c.addSymbol(2, new Constant(new MultiEvent(a, b)));
			invert_b_c.addSymbol(3, new Constant(new MultiEvent(c)));
		}
		EnforcementPipeline pipeline = new EnforcementPipeline(invert_b_c, new NoTwoBs(), new HighestString());
		SinkLast sink = new SinkLast();
		Connector.connect(pipeline, sink);
		Event e = null;
		Pushable p = pipeline.getPushableInput();
		p.push(a);
		e = (Event) sink.getLast()[0];
		assertEquals("a", e.getLabel());
		p.push(b);
		e = (Event) sink.getLast()[0];
		assertEquals("b", e.getLabel());
		p.push(b);
		e = (Event) sink.getLast()[0];
		assertEquals("a", e.getLabel());
	}
}
