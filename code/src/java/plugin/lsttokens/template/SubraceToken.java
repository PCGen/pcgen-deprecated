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
import pcgen.cdom.enumeration.SubRace;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SUBRACE Token
 */
public class SubraceToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "SUBRACE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		String subrace = value;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.equalsIgnoreCase("YES"))
			{
				subrace = template.getDisplayName();
			}
			else
			{
				Logging.deprecationPrint("You should use 'YES' as the "
					+ getTokenName());
				Logging
					.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
		}

		template.setSubRace(subrace);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		String subrace = value;
		context.obj.put(template, ObjectKey.SUBRACE, SubRace
			.getConstant(subrace));
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		SubRace subRace = context.obj.getObject(pct, ObjectKey.SUBRACE);
		if (subRace == null)
		{
			return null;
		}
		return new String[]{subRace.toString()};
	}

}
