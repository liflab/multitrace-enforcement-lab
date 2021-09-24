package multitrace.lab.casino;

import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tkltl.OperatorC;
import multitrace.Quadrilean;

public class MaximizeBets extends OperatorC
{
	public MaximizeBets()
	{
		super(new ApplyFunction(CasinoFunction.isBet), Quadrilean.Value.TRUE);
	}
}
