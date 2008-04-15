/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.content;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;

public class TextProperty extends ConcretePrereqObject
{

	private List<Formula> formulaList = null;

	public TextProperty()
	{
		super();
	}

	public void addVariable(Formula f)
	{
		if (formulaList == null)
		{
			formulaList = new ArrayList<Formula>(3);
		}
		formulaList.add(f);
	}

	public int getVariableCount()
	{
		return formulaList == null ? 0 : formulaList.size();
	}

	public Formula getVariable(int i)
	{
		return formulaList.get(i);
	}

	/*
	 * CONSIDER Do I need some check to ensure that the appropriate # of
	 * formulae are added?
	 */

	protected boolean matchesFormulaList(TextProperty tp)
	{
		return formulaList == null && tp.formulaList == null
			|| formulaList != null && formulaList.equals(tp.formulaList);
	}
}
