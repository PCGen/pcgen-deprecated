/*
 * DisplayNameToken.java
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

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.AbilityCategory;
import pcgen.persistence.lst.AbilityCategoryLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Handles the DISPLAYNAME token on an ABILITYCATEGORY line.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class DisplayNameToken implements AbilityCategoryLstToken,
		CDOMPrimaryToken<CDOMAbilityCategory>
{

	/**
	 * @see pcgen.persistence.lst.AbilityCategoryLstToken#parse(pcgen.core.AbilityCategory,
	 *      java.lang.String)
	 */
	public boolean parse(final AbilityCategory aCat, final String aValue)
	{
		aCat.setName(aValue);
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "DISPLAYNAME"; //$NON-NLS-1$
	}

	public boolean parse(LoadContext context, CDOMAbilityCategory eq,
			String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " argument may not be empty");
			return false;
		}
		context.getObjectContext().put(eq, StringKey.DISPLAY_NAME, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMAbilityCategory eq)
	{
		String rof = context.getObjectContext().getString(eq,
				StringKey.DISPLAY_NAME);
		if (rof == null)
		{
			return null;
		}
		return new String[] { rof };
	}

	public Class<CDOMAbilityCategory> getTokenClass()
	{
		return CDOMAbilityCategory.class;
	}
}
