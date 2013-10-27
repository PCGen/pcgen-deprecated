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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSSKILLPOINTS Token
 */
public class BonusskillpointsToken implements PCTemplateLstToken,
		CDOMPrimaryToken<CDOMTemplate>
{

	public String getTokenName()
	{
		return "BONUSSKILLPOINTS";
	}

	// additional skill points per level
	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			template.setBonusSkillsPerLevel(Integer.parseInt(value));
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMTemplate template,
			String value)
	{
		try
		{
			int skillCount = Integer.parseInt(value);
			if (skillCount <= 0)
			{
				Logging.errorPrint(getTokenName()
						+ " must be an integer greater than zero");
				return false;
			}
			context.getObjectContext().put(template,
					IntegerKey.BONUS_CLASS_SKILL_POINTS, skillCount);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
					+ value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMTemplate pct)
	{
		Integer points = context.getObjectContext().getInteger(pct,
				IntegerKey.BONUS_CLASS_SKILL_POINTS);
		if (points == null)
		{
			return null;
		}
		if (points.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { points.toString() };
	}

	public Class<CDOMTemplate> getTokenClass()
	{
		return CDOMTemplate.class;
	}
}
