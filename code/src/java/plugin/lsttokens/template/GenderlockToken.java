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

import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with GENDERLOCK Token
 */
public class GenderlockToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "GENDERLOCK";
	}

	// set and lock character gender, disabling pulldown menu in description
	// section.
	public boolean parse(PCTemplate template, String value)
	{
		template.setGenderLock(value);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			context.getObjectContext().put(template, ObjectKey.GENDER_LOCK,
				Gender.valueOf(value));
			return true;
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Gender provided in " + getTokenName()
				+ ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Gender g =
				context.getObjectContext()
					.getObject(pct, ObjectKey.GENDER_LOCK);
		if (g == null)
		{
			return null;
		}
		return new String[]{g.toString()};
	}
}
