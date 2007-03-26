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

import java.util.Set;

import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.content.LevelSkillPoints;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.Aggregator;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
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

		CDOMGroupRef<PCClass> owner =
				context.ref.getCDOMAllReference(PCClass.class);
		Aggregator ag = new Aggregator(template, owner, getTokenName());
		LevelSkillPoints lsp = new LevelSkillPoints(skillCount);
		context.graph.linkAllowIntoGraph(getTokenName(), template, ag);
		context.graph.linkActivationIntoGraph(getTokenName(), owner, ag);
		context.graph.linkObjectIntoGraph(getTokenName(), ag, lsp);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					Aggregator.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		if (edges.size() != 1)
		{
			context.addWriteMessage("Only 1 " + getTokenName()
				+ " is allowed per Template");
			return null;
		}
		// CONSIDER Verify this is an allow edge?
		Aggregator ag =
				(Aggregator) edges.iterator().next().getSinkNodes().get(0);
		Set<PCGraphEdge> links =
				context.graph.getChildLinksFromToken(getTokenName(), ag,
					LevelSkillPoints.class);
		if (links == null || links.isEmpty())
		{
			return null;
		}
		if (links.size() > 1)
		{
			context.addWriteMessage("Only  1 " + getTokenName()
				+ " is allowed per Aggregator");
			return null;
		}
		LevelSkillPoints lsp =
				(LevelSkillPoints) links.iterator().next().getSinkNodes()
					.get(0);
		// CONSIDER Verify the rest of the structure??
		return new String[]{Integer.toString(lsp.getPoints())};
	}
}
