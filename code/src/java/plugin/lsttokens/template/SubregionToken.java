/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.template;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SUBREGION Token
 */
public class SubregionToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "SUBREGION";
	}

	public boolean parse(PCTemplate template, String value)
	{
		String subregion = value;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.equalsIgnoreCase("YES"))
			{
				subregion = template.getDisplayName();
			}
			else
			{
				Logging.deprecationPrint("You should use 'YES' as the "
					+ getTokenName());
				Logging
					.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
		}
		template.setSubRegion(subregion);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		String subregion = value;
		context.obj.put(template, ObjectKey.SUBREGION, SubRegion
			.getConstant(subregion));
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		SubRegion targetArea = context.obj.getObject(pct, ObjectKey.SUBREGION);
		if (targetArea == null)
		{
			return null;
		}
		return new String[]{targetArea.toString()};
	}
}
