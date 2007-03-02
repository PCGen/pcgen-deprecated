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
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.util.Logging;

/**
 * Class deals with USEUNTRAINED Token
 */
public class UseuntrainedToken implements SkillLstToken
{

	public String getTokenName()
	{
		return "USEUNTRAINED";
	}

	public boolean parse(Skill skill, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			set = true;
		}
		else
		{
			set = false;
		}
		skill.setUntrained(set);
		return true;
	}

	public boolean parse(LoadContext context, Skill skill, String value)
		throws PersistenceLayerException
	{
		if (value.equalsIgnoreCase("NO"))
		{
			skill.put(ObjectKey.USE_UNTRAINED, Boolean.FALSE);
		}
		else if (value.equalsIgnoreCase("YES"))
		{
			skill.put(ObjectKey.USE_UNTRAINED, Boolean.TRUE);
		}
		else
		{
			Logging.errorPrint("Did not understand " + getTokenName()
				+ " value: " + value);
			Logging.errorPrint("Must be YES or NO");
			return false;
		}
		return true;
	}

	public String unparse(LoadContext context, Skill skill)
	{
		Boolean useUntrained = skill.get(ObjectKey.USE_UNTRAINED);
		if (useUntrained == null)
		{
			return null;
		}
		return new StringBuilder().append(getTokenName()).append(':').append(
			useUntrained.booleanValue() ? "YES" : "NO").toString();
	}
}
