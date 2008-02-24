/*
 * AbilityToken.java
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
 * Created on March 3, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.basekit;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.kit.CDOMKitGear;
import pcgen.core.kit.BaseKit;
import pcgen.persistence.lst.BaseKitLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

/**
 * LOOKUP token for base kits
 */
public class LookupToken implements BaseKitLstToken,
		CDOMSecondaryToken<CDOMKitGear>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "LOOKUP";
	}

	public boolean parse(BaseKit baseKit, String value)
	{
		baseKit.addLookup(value);
		return true;
	}

	public Class<CDOMKitGear> getTokenClass()
	{
		return CDOMKitGear.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitGear kitGear, String value)
	{
		int commaLoc = value.indexOf(',');
		if (commaLoc == -1)
		{
			return false;
		}
		if (commaLoc != value.lastIndexOf(','))
		{
			return false;
		}
		String tableEntry = value.substring(0, commaLoc);
		Formula f = FormulaFactory.getFormulaFor(value.substring(commaLoc + 1));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitGear kitGear)
	{
		return null;
	}
}
