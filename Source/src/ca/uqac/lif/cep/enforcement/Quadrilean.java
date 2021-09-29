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
package ca.uqac.lif.cep.enforcement;

import java.util.Collection;

import ca.uqac.lif.cep.functions.UnaryFunction;

public class Quadrilean
{
	/**
	 * The four possible truth values.
	 */
	public enum Value {TRUE, P_TRUE, P_FALSE, FALSE}
	
	public static class QuadrileanCast extends UnaryFunction<Object,Quadrilean.Value>
	{
		public static final QuadrileanCast instance = new QuadrileanCast();
		
		protected QuadrileanCast()
		{
			super(Object.class, Quadrilean.Value.class);
		}

		@Override
		public Value getValue(Object x)
		{
			if (x instanceof Boolean)
			{
				Boolean b = (Boolean) x;
				if (b)
				{
					return Quadrilean.Value.TRUE;
				}
				return Quadrilean.Value.FALSE;
			}
			return Quadrilean.Value.FALSE;
		}
		
		@Override
		public QuadrileanCast duplicate(boolean with_state)
		{
			return this;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static class BagAnd extends UnaryFunction<Collection,Quadrilean.Value>
	{
		public static final transient BagAnd instance = new BagAnd();
		
		protected BagAnd()
		{
			super(Collection.class, Quadrilean.Value.class);
		}

		@Override
		public Value getValue(Collection col)
		{
			Value v = Value.TRUE;
			for (Object o : col)
			{
				v = and(v, o);
				if (v == Value.FALSE)
				{
					return v;
				}
			}
			return v;
		}
	}
	
	public static Value toQuadrilean(Object x)
	{
		if (x instanceof Quadrilean.Value)
		{
			return (Quadrilean.Value) x;
		}
		if (x instanceof Boolean)
		{
			Boolean b = (Boolean) x;
			if (b)
			{
				return Quadrilean.Value.TRUE;
			}
			return Quadrilean.Value.FALSE;
		}
		return Quadrilean.Value.FALSE;
	}
	
	public static Value and(Object o1, Object o2)
	{
		Value v1 = toQuadrilean(o1);
		Value v2 = toQuadrilean(o2);
		switch (v1)
		{
		case TRUE:
			return v2;
		case P_TRUE:
			if (v2 == Value.TRUE)
			{
				return v1;
			}
			return v2;
		case P_FALSE:
			if (v1 == Value.FALSE)
			{
				return v1;
			}
			return Value.P_FALSE;
		default:
			return Value.FALSE;
		}
	}
}
