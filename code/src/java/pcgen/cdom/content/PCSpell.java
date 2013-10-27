/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.spell.Spell;

public class PCSpell extends ConcretePrereqObject
{

	private Spell spell;

	private String spellBook;

	private Formula casterLevel;

	private Formula times;

	private Formula dc;

	public Formula getCasterLevel()
	{
		return casterLevel;
	}

	public void setCasterLevel(Formula cLvl)
	{
		casterLevel = cLvl;
	}

	public Formula getDC()
	{
		return dc;
	}

	public void setDC(Formula dcFormula)
	{
		dc = dcFormula;
	}

	public Spell getSpell()
	{
		return spell;
	}

	public void setSpell(Spell sp)
	{
		spell = sp;
	}

	public String getSpellBook()
	{
		return spellBook;
	}

	public void setSpellBook(String spBook)
	{
		spellBook = spBook;
	}

	public Formula getTimesPerDay()
	{
		return times;
	}

	public void setTimesPerDay(Formula timesFormula)
	{
		times = timesFormula;
	}

}
