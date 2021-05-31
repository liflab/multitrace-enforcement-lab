package ca.uqac.lif.cep.tkltl;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Stutter;

public class StutterTest
{

	public static void main(String[] args)
	{
		Stutter st = new Stutter(3);
		Print print = new Print();
		Connector.connect(st, print);
		st.getPushableInput().push("A");

	}

}
