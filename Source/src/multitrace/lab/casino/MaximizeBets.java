package multitrace.lab.casino;

import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.tkltl.OperatorC;

public class MaximizeBets extends OperatorC
{
	public MaximizeBets()
	{
		super(new ApplyFunction(CasinoFunction.isBet), Troolean.Value.TRUE);
	}
}
