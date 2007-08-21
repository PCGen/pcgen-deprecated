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
package plugin.lsttokens.skill;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.util.Logging;

/**
 * Class deals with EXCLUSIVE Token
 */
public class ExclusiveToken implements SkillLstToken
{

	public String getTokenName()
	{
		return "EXCLUSIVE";
	}

	public boolean parse(Skill skill, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
					+ getTokenName());
				Logging
					.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = true;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n'
				&& !value.equalsIgnoreCase("NO"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
					+ getTokenName());
				Logging
					.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = false;
		}
		skill.setIsExclusive(set);
		return true;
	}

	public boolean parse(LoadContext context, Skill skill, String value)
	{
		Boolean exclusive;
		if (value.equalsIgnoreCase("NO"))
		{
			exclusive = Boolean.FALSE;
		}
		else if (value.equalsIgnoreCase("YES"))
		{
			exclusive = Boolean.TRUE;
		}
		else
		{
			Logging.errorPrint("Did not understand " + getTokenName()
				+ " value: " + value);
			Logging.errorPrint("Must be YES or NO");
			return false;
		}
		context.getObjectContext().put(skill, ObjectKey.EXCLUSIVE, exclusive);
		return true;
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		Boolean exclusive =
				context.getObjectContext()
					.getObject(skill, ObjectKey.EXCLUSIVE);
		if (exclusive == null)
		{
			return null;
		}
		return new String[]{exclusive.booleanValue() ? "YES" : "NO"};
	}
}
