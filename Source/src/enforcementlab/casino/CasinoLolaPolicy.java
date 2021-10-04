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
package enforcementlab.casino;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.enforcement.Quadrilean.BooleanCast;
import ca.uqac.lif.cep.enforcement.Quadrilean.QuadrileanCast;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.polyglot.lola.LolaInterpreter;
import ca.uqac.lif.cep.polyglot.lola.NamedGroupProcessor;
import ca.uqac.lif.cep.tmf.Fork;

public class CasinoLolaPolicy extends GroupProcessor
{
	/**
	 * The name given to this policy.
	 */
	public static final String NAME = "Casino policy";

	/**
	 * The LOLA interpreter used to parse the policy.
	 */
	protected static transient LolaInterpreter s_interpreter;

	static
	{
		try
		{
			s_interpreter = new LolaInterpreter();
		}
		catch (InvalidGrammarException e)
		{
			// Won't happen
		}
	}
	
	public CasinoLolaPolicy()
	{
		this(false);
	}


	public CasinoLolaPolicy(boolean debug)
	{
		super(1, debug ? 3 : 1);
		try
		{
			Fork f = new Fork(4);
			ApplyFunction p_e = new ApplyFunction(new FunctionTree(BooleanCast.instance, CasinoFunction.isEnd));
			ApplyFunction p_b = new ApplyFunction(new FunctionTree(BooleanCast.instance, CasinoFunction.isBet));
			ApplyFunction p_pp = new ApplyFunction(new FunctionTree(BooleanCast.instance, CasinoFunction.casinoPaid));
			ApplyFunction p_pm = new ApplyFunction(new FunctionTree(BooleanCast.instance, CasinoFunction.casinoPays));
			Connector.connect(f, 0, p_e, 0);
			Connector.connect(f, 1, p_b, 0);
			Connector.connect(f, 2, p_pp, 0);
			Connector.connect(f, 3, p_pm, 0);
			String filename = debug ? "policy-debug.lola" : "policy.lola";
			LolaInterpreter interpreter = new LolaInterpreter();
			NamedGroupProcessor ngp = (NamedGroupProcessor) interpreter.build(CasinoLolaPolicy.class.getResourceAsStream(filename));
			int e = ngp.getInputIndex("e");
			int b = ngp.getInputIndex("b");
			int pp = ngp.getInputIndex("pp");
			int pm = ngp.getInputIndex("pm");
			Connector.connect(p_e, 0, ngp, e);
			Connector.connect(p_b, 0, ngp, b);
			Connector.connect(p_pp, 0, ngp, pp);
			Connector.connect(p_pm, 0, ngp, pm);
			ApplyFunction qc = new ApplyFunction(QuadrileanCast.instance);
			Connector.connect(ngp, 0, qc, 0);
			addProcessors(f, p_e, p_b, p_pp, p_pm, ngp, qc);
			associateInput(0, f, 0);
			associateOutput(ngp.getOutputIndex("phi"), qc, 0);
			if (debug)
			{
				associateOutput(ngp.getOutputIndex("t1"), ngp, 1);
				associateOutput(ngp.getOutputIndex("t2"), ngp, 2);
			}
		}
		catch (BuildException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidGrammarException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
