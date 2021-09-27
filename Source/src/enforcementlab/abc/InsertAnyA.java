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
package enforcementlab.abc;

import ca.uqac.lif.cep.enforcement.Event;
import ca.uqac.lif.cep.enforcement.proxy.InsertAny;

/**
 * A type of {@link InsertAny} that only inserts a's.
 */
public class InsertAnyA extends InsertAny
{
	public static final transient String NAME = "Insert any a";
	
	public InsertAnyA()
	{
		super(Event.get("a"));
	}
}
