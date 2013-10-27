/*
 * CountToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.spells;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.kit.CDOMKitSpells;
import pcgen.core.kit.KitSpells;
import pcgen.persistence.lst.KitSpellsLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

/**
 * COUNT Token for KitSpells
 */
public class CountToken implements KitSpellsLstToken,
		CDOMSecondaryToken<CDOMKitSpells>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "COUNT";
	}

	/**
	 * parse
	 * 
	 * @param kitSpells
	 *            KitSpells
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitSpells kitSpells, String value)
	{
		kitSpells.setCountFormula(value);
		return true;
	}

	public Class<CDOMKitSpells> getTokenClass()
	{
		return CDOMKitSpells.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitSpells kitSpells,
			String value)
	{
		kitSpells.setCount(FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitSpells kitSpells)
	{
		Formula bd = kitSpells.getCount();
		if (bd == null)
		{
			return null;
		}
		return new String[] { bd.toString() };
	}
}
