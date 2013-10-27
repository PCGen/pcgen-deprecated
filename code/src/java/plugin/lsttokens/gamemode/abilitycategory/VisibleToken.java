/*
 * VisibleToken.java
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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.AbilityCategory;
import pcgen.persistence.lst.AbilityCategoryLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Handles the VISIBLE token on an ABILITYCATEGORY line.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class VisibleToken implements AbilityCategoryLstToken, CDOMPrimaryToken<CDOMAbilityCategory>
{
	/**
	 * @see pcgen.persistence.lst.AbilityCategoryLstToken#parse(pcgen.core.AbilityCategory,
	 *      java.lang.String)
	 */
	public boolean parse(final AbilityCategory aCat, final String aValue)
	{
		if ((aValue.length() > 0) && (aValue.charAt(0) == 'Y'))
		{
			if (!aValue.equals("YES"))
			{
				Logging
						.deprecationPrint("Abbreviation used in VISIBLE in AbilityCategory");
				Logging.deprecationPrint(" " + aValue
						+ " is not a valid value for VISIBLE");
				Logging
						.deprecationPrint(" Valid values in AbilityCategory are NO, QUALIFY and YES");
				Logging
						.deprecationPrint(" assuming you meant YES, please use YES (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			aCat.setVisible(AbilityCategory.VISIBLE_YES);
		}
		else if ((aValue.length() > 0) && (aValue.charAt(0) == 'Q'))
		{
			if (!aValue.equals("QUALIFY"))
			{
				Logging
						.deprecationPrint("Abbreviation used in VISIBLE in AbilityCategory");
				Logging.deprecationPrint(" " + aValue
						+ " is not a valid value for VISIBLE");
				Logging
						.deprecationPrint(" Valid values in AbilityCategory are NO, QUALIFY and YES");
				Logging
						.deprecationPrint(" assuming you meant QUALIFY, please use QUALIFY (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			aCat.setVisible(AbilityCategory.VISIBLE_QUALIFIED);
		}
		else if ((aValue.length() > 0) && (aValue.charAt(0) == 'N'))
		{
			if (!aValue.equals("NO"))
			{
				Logging
						.deprecationPrint("Abbreviation used in VISIBLE in AbilityCategory");
				Logging.deprecationPrint(" " + aValue
						+ " is not a valid value for VISIBLE");
				Logging
						.deprecationPrint(" Valid values in AbilityCategory are NO, QUALIFY and YES");
				Logging
						.deprecationPrint(" assuming you meant NO, please use NO (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			aCat.setVisible(AbilityCategory.VISIBLE_NO);
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE"; //$NON-NLS-1$
	}

	public boolean parse(LoadContext context, CDOMAbilityCategory ability,
			String value)
	{
		Visibility vis;
		if (value.equals("YES"))
		{
			vis = Visibility.YES;
		}
		else if (value.equals("QUALIFY"))
		{
			vis = Visibility.QUALIFY;
		}
		else if (value.equals("NO"))
		{
			vis = Visibility.NO;
		}
		else
		{
			Logging.errorPrint("Unable to understand " + getTokenName()
					+ " tag: " + value);
			return false;
		}
		context.getObjectContext().put(ability, ObjectKey.VISIBILITY, vis);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMAbilityCategory ability)
	{
		Visibility vis = context.getObjectContext().getObject(ability,
				ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.YES))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.QUALIFY))
		{
			visString = "QUALIFY";
		}
		else if (vis.equals(Visibility.NO))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
					+ " is not a valid Visibility for an Ability");
			return null;
		}
		return new String[] { visString };
	}

	public Class<CDOMAbilityCategory> getTokenClass()
	{
		return CDOMAbilityCategory.class;
	}
}
