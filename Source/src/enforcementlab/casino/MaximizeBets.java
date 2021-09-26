package enforcementlab.casino;

import ca.uqac.lif.cep.enforcement.Quadrilean;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tkltl.OperatorC;

public class MaximizeBets extends OperatorC
{
	public MaximizeBets()
	{
		super(new ApplyFunction(CasinoFunction.isBet), Quadrilean.Value.TRUE);
	}
}
