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
import pcgen.cdom.enumeration.Region;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with REGION Token
 */
public class RegionToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "REGION";
	}

	public boolean parse(PCTemplate template, String value)
	{
		String region = value;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.equalsIgnoreCase("YES"))
			{
				region = template.getDisplayName();
			}
			else
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
					+ getTokenName());
				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
			}
		}
		else if (value.equalsIgnoreCase("NO"))
		{
			Logging.errorPrint("You should use 'YES' or 'NO' as the "
				+ getTokenName());
			Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
		}

		template.setRegion(region);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		String region = value;
		context.obj.put(template, ObjectKey.REGION, Region.getConstant(region));
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Region targetArea = context.obj.getObject(pct, ObjectKey.REGION);
		if (targetArea == null)
		{
			return null;
		}
		return new String[]{targetArea.toString()};
	}

}
