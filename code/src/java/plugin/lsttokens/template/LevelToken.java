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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.content.SpecialAbility;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.PCTemplate;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.persistence.lst.TokenStore;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with LEVEL Token
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2007-01-03 02:53:55 -0500
 * (Wed, 03 Jan 2007) $
 * 
 * @version $Revision$
 */
public class LevelToken extends AbstractToken implements PCTemplateLstToken
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "LEVEL";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.PCTemplateLstToken#parse(pcgen.core.PCTemplate,
	 *      java.lang.String)
	 */
	public boolean parse(PCTemplate template, String value)
	{
		if (".CLEAR".equals(value))
		{
			template.clearLevelAbilities();
			return true;
		}

		final StringTokenizer tok = new StringTokenizer(value, ":");
		final String levelStr = tok.nextToken();
		final int level;
		try
		{
			level = Integer.parseInt(levelStr);
		}
		catch (NumberFormatException ex)
		{
			// TODO - Add error message.
			return false;
		}
		final String typeStr = tok.nextToken();

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PCTemplateLstToken.class);
		PCTemplateLstToken token = (PCTemplateLstToken) tokenMap.get(typeStr);

		if (token != null)
		{
			template.addLevelAbility(level, typeStr, tok.nextToken());
		}
		else
		{
			String tagValue = value.substring(levelStr.length() + 1);
			try
			{
				return PObjectLoader.parseTagLevel(template, tagValue, level);
			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Failed to parse " + value + ".", e);
				return false;
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		if (".CLEAR".equals(value))
		{
			context.graph.removeAll(getTokenName(), template);
			return true;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.COLON);

		final String levelStr = tok.nextToken();
		try
		{
			/*
			 * Note this test of integer (even if it doesn't get used outside
			 * this try) is necessary for catching errors.
			 */
			int lvl = Integer.parseInt(levelStr);
			if (lvl <= 0)
			{
				Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Level was <= 0): " + lvl);
				Logging.errorPrint("  Line was: " + value);
				return false;
			}
		}
		catch (NumberFormatException ex)
		{
			Logging.errorPrint("Misunderstood Level value: " + levelStr
				+ " in " + getTokenName());
			return false;
		}

		if (!tok.hasMoreTokens())
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": requires 3 colon separated elements (has one): " + value);
			return false;
		}
		final String typeStr = tok.nextToken();
		if (!tok.hasMoreTokens())
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": requires 3 colon separated elements (has two): " + value);
			return false;
		}

		PrereqObject pro;
		if ("DR".equals(typeStr))
		{
			pro = TokenUtilities.getDamageReduction(tok.nextToken());
			if (pro == null)
			{
				Logging.errorPrint("Invalid " + getTokenName()
					+ ": DR was not valid: " + value);
				return false;
			}
		}
		else if ("SR".equals(typeStr))
		{
			pro =
					new SpellResistance(FormulaFactory.getFormulaFor(tok
						.nextToken()));
		}
		else if ("SA".equals(typeStr))
		{
			/*
			 * TODO FIXME This is insufficient (doesn't handle variables)
			 */
			pro = new SpecialAbility(tok.nextToken());
		}
		else if ("CR".equals(typeStr))
		{
			pro = new ChallengeRating(tok.nextToken());
		}
		else
		{
			Logging.errorPrint("Misunderstood Type in " + getTokenName() + ": "
				+ typeStr + ". Tag was: " + value);
			return false;
		}

		PCGraphEdge edge =
				context.graph.grant(getTokenName(), template, pro);
		edge.addPrerequisite(getPrerequisite("PRELEVEL:" + levelStr));

		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), pct);
		if (edges.isEmpty())
		{
			return null;
		}

		Set<String> set = new TreeSet<String>();

		for (PCGraphEdge edge : edges)
		{
			if (edge.getPrerequisiteCount() != 1)
			{
				context.addWriteMessage("Only one Prerequisiste allowed on "
					+ getTokenName() + " derived edge");
				return null;
			}
			Prerequisite prereq = edge.getPrerequisiteList().get(0);
			String kind = prereq.getKind();
			if (!kind.equalsIgnoreCase("LEVEL"))
			{
				context.addWriteMessage("Prerequisiste on " + getTokenName()
					+ " derived edge must be LEVEL");
				return null;
			}
			if (!PrerequisiteOperator.GTEQ.equals(prereq.getOperator()))
			{
				context.addWriteMessage("Invalid Operator built on "
					+ getTokenName() + " derived edge");
				return null;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(prereq.getOperand());
			List<PrereqObject> sinkNodes = edge.getSinkNodes();
			if (sinkNodes == null || sinkNodes.size() != 1)
			{
				context.addWriteMessage("Edge derived from " + getTokenName()
					+ " must have only one sink");
				return null;
			}
			PrereqObject sink = sinkNodes.get(0);
			if (sink instanceof DamageReduction)
			{
				sb.append(":DR:").append(sink);
			}
			else if (sink instanceof SpellResistance)
			{
				sb.append(":SR:").append(sink);
			}
			else if (sink instanceof SpecialAbility)
			{
				sb.append(":SA:").append(((SpecialAbility) sink).toLSTform());
			}
			else if (sink instanceof ChallengeRating)
			{
				sb.append(":CR:").append(((ChallengeRating) sink).getLSTformat());
			}
			else
			{
				context.addWriteMessage("Cannot write "
					+ sink.getClass().getSimpleName() + " from "
					+ getTokenName());
				return null;
			}
			set.add(sb.toString());
		}
		return set.toArray(new String[set.size()]);
	}
}
