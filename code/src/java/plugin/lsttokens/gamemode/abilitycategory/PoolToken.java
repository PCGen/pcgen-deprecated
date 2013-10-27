/*
 * PoolToken.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package plugin.lsttokens.gamemode.abilitycategory;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.AbilityCategory;
import pcgen.persistence.lst.AbilityCategoryLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Handles the POOL token on an ABILITYCATEGORY line.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class PoolToken implements AbilityCategoryLstToken,
		CDOMPrimaryToken<CDOMAbilityCategory>
{

	/**
	 * @see pcgen.persistence.lst.AbilityCategoryLstToken#parse(pcgen.core.AbilityCategory,
	 *      java.lang.String)
	 */
	public boolean parse(final AbilityCategory aCat, final String aValue)
	{
		// TODO - Add a validate() method to VariableProcessor
		aCat.setPoolFormula(aValue);
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "POOL"; //$NON-NLS-1$
	}

	public boolean parse(LoadContext context, CDOMAbilityCategory stat,
			String value)
	{
		context.getObjectContext().put(stat, FormulaKey.POOL,
				FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMAbilityCategory stat)
	{
		Formula f = context.getObjectContext()
				.getFormula(stat, FormulaKey.POOL);
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}

	public Class<CDOMAbilityCategory> getTokenClass()
	{
		return CDOMAbilityCategory.class;
	}
}
