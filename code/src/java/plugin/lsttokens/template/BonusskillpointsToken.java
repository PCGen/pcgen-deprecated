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

import java.util.Collection;

import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.ClassSkillPointFactory;
import pcgen.core.PCTemplate;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSSKILLPOINTS Token
 */
public class BonusskillpointsToken implements PCTemplateLstToken
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

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		int skillCount;
		try
		{
			skillCount = Integer.parseInt(value);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
				+ value);
			return false;
		}
		if (skillCount <= 0)
		{
			Logging.errorPrint(getTokenName()
				+ " must be an integer greater than zero");
			return false;
		}

		ClassSkillPointFactory cf = new ClassSkillPointFactory(skillCount);
		context.graph.grant(getTokenName(), template, cf);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		GraphChanges<ClassSkillPointFactory> changes =
				context.graph.getChangesFromToken(getTokenName(), pct,
					ClassSkillPointFactory.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no BONUSSKILLPOINTS
			return null;
		}
		if (added.size() > 1)
		{
			// TODO Error message
			return null;
		}
		ClassSkillPointFactory lcf =
				(ClassSkillPointFactory) added.iterator().next();
		int skillPoints = lcf.getSkillPointCount();
		if (skillPoints <= 0)
		{
			context.addWriteMessage("Number of skill points granted in "
				+ getTokenName() + " must be greater than zero");
			return null;
		}
		return new String[]{Integer.toString(skillPoints)};
	}
}
