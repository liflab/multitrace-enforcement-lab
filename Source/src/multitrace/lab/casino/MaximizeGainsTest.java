package multitrace.lab.casino;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.SinkLast;

public class MaximizeGainsTest
{
	@Test
	public void test1()
	{
		MaximizeGains mg = new MaximizeGains();
		SinkLast sl = new SinkLast();
		Connector.connect(mg, sl);
		Pushable p = mg.getPushableInput();
		p.push(new CasinoEvent.Pay("A", "casino"));
		assertEquals(1, (float) sl.getLast()[0], 0.1);
		p.push(new CasinoEvent.Pay("B", "casino"));
		assertEquals(2, (float) sl.getLast()[0], 0.1);
		p.push(new CasinoEvent.Pay("casino", "C"));
		assertEquals(1, (float) sl.getLast()[0], 0.1);
	}
}
