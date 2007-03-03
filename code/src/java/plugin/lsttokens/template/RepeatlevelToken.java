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
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.content.SpecialAbility;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.Aggregator;
import pcgen.core.PCTemplate;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with REPEATLEVEL Token
 */
public class RepeatlevelToken extends AbstractToken implements
		PCTemplateLstToken
{

	@Override
	public String getTokenName()
	{
		return "REPEATLEVEL";
	}

	public boolean parse(PCTemplate template, String value)
	{
		//
		// x|y|z:level:<level assigned item>
		//
		final int endRepeat = value.indexOf(':');
		if (endRepeat > 0)
		{
			final int endLevel = value.indexOf(':', endRepeat + 1);
			if (endLevel > 0)
			{
				final StringTokenizer repeatToken =
						new StringTokenizer(value.substring(0, endRepeat), "|");
				if (repeatToken.countTokens() == 3)
				{
					try
					{
						final int lvlIncrement =
								Integer.parseInt(repeatToken.nextToken());
						final int consecutive =
								Integer.parseInt(repeatToken.nextToken());
						final int maxLevel =
								Integer.parseInt(repeatToken.nextToken());
						int iLevel =
								Integer.parseInt(value.substring(endRepeat + 1,
									endLevel));

						if ((iLevel > 0) && (lvlIncrement > 0)
							&& (maxLevel > 0) && (consecutive >= 0))
						{
							int count = consecutive;
							for (; iLevel <= maxLevel; iLevel += lvlIncrement)
							{
								if ((consecutive == 0) || (count != 0))
								{
									final StringTokenizer tok =
											new StringTokenizer(value
												.substring(endLevel + 1));
									final String type = tok.nextToken();

									template.addLevelAbility(iLevel, type, tok
										.nextToken());
								}
								if (consecutive != 0)
								{
									if (count == 0)
									{
										count = consecutive;
									}
									else
									{
										--count;
									}
								}
							}
						}

						return true;
					}
					catch (NumberFormatException nfe)
					{
						return false;
					}
				}
			}
		}
		return false;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		//
		// x|y|z:level:<level assigned item>
		//
		int endRepeat = value.indexOf(Constants.COLON);
		if (endRepeat < 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (No Colon): " + value);
			return false;
		}
		int endLevel = value.indexOf(Constants.COLON, endRepeat + 1);
		if (endLevel < 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Only One Colon): " + value);
			return false;
		}
		int endAssignType = value.indexOf(Constants.COLON, endLevel + 1);
		if (endAssignType == -1)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Only Two Colons): " + value);
			return false;
		}

		String repeatedInfo = value.substring(0, endRepeat);
		StringTokenizer repeatToken =
				new StringTokenizer(repeatedInfo, Constants.PIPE);
		if (repeatToken.countTokens() != 3)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (incorrect PIPE count in repeat): " + repeatedInfo);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String levelIncrement = repeatToken.nextToken();
		int lvlIncrement;
		try
		{
			lvlIncrement = Integer.parseInt(levelIncrement);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Level Increment was not an Integer): "
				+ levelIncrement);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (lvlIncrement <= 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Level Increment was <= 0): " + lvlIncrement);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String consecutiveString = repeatToken.nextToken();
		int consecutive;
		try
		{
			consecutive = Integer.parseInt(consecutiveString);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Consecutive Value was not an Integer): "
				+ consecutiveString);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (consecutive <= 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Consecutive String was <= 0): " + consecutive);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String maxLevelString = repeatToken.nextToken();
		int maxLevel;
		try
		{
			maxLevel = Integer.parseInt(maxLevelString);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Max Level was not an Integer): " + maxLevelString);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (maxLevel <= 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Max Level was <= 0): " + maxLevel);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String levelString = value.substring(endRepeat + 1, endLevel);
		int iLevel;
		try
		{
			iLevel = Integer.parseInt(levelString);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Level was not a number): " + levelString);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (iLevel <= 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Level was <= 0): " + iLevel);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String typeStr = value.substring(endLevel + 1, endAssignType);
		String contentStr = value.substring(endAssignType + 1);
		PrereqObject pro;
		if ("DR".equals(typeStr))
		{
			pro = TokenUtilities.getDamageReduction(contentStr);
		}
		else if ("SR".equals(typeStr))
		{
			pro = new SpellResistance(FormulaFactory.getFormulaFor(contentStr));
		}
		else if ("SA".equals(typeStr))
		{
			pro = new SpecialAbility(contentStr);
		}
		else if ("CR".equals(typeStr))
		{
			pro = new ChallengeRating(contentStr);
		}
		else
		{
			Logging.errorPrint("Misunderstood Type in " + getTokenName() + ": "
				+ typeStr + ". Tag was: " + value);
			return false;
		}

		Aggregator agg = new Aggregator(template, getTokenName());
		agg.put(IntegerKey.CONSECUTIVE, Integer.valueOf(consecutive));
		agg.put(IntegerKey.MAX_LEVEL, Integer.valueOf(maxLevel));
		agg.put(IntegerKey.LEVEL_INCREMENT, Integer.valueOf(lvlIncrement));
		agg.put(IntegerKey.START_LEVEL, Integer.valueOf(iLevel));

		context.graph.linkObjectIntoGraph(getTokenName(), template, agg);

		for (int count = consecutive; iLevel <= maxLevel; iLevel +=
				lvlIncrement)
		{
			if ((consecutive == 0) || (count != 0))
			{
				PCGraphEdge edge =
						context.graph.linkObjectIntoGraph(getTokenName(), agg,
							pro);
				edge.addAllPrerequisites(getPrerequisite("PRELEVEL:" + iLevel));
			}
			if (consecutive != 0)
			{
				if (count == 0)
				{
					count = consecutive;
				}
				else
				{
					--count;
				}
			}
		}
		return true;
	}

	public String unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					Aggregator.class);
		StringBuilder sb = new StringBuilder();
		boolean needsTab = false;
		for (PCGraphEdge edge : edgeList)
		{
			if (needsTab)
			{
				sb.append('\t');
			}
			Aggregator agg = (Aggregator) edge.getSinkNodes().get(0);
			Integer consecutive = agg.get(IntegerKey.CONSECUTIVE);
			Integer maxLevel = agg.get(IntegerKey.MAX_LEVEL);
			Integer lvlIncrement = agg.get(IntegerKey.LEVEL_INCREMENT);
			Integer iLevel = agg.get(IntegerKey.START_LEVEL);
			sb.append(lvlIncrement).append(Constants.PIPE);
			sb.append(consecutive).append(Constants.PIPE);
			sb.append(maxLevel).append(Constants.COLON);
			sb.append(iLevel).append(Constants.COLON);
			Set<PCGraphEdge> subEdgeList =
					context.graph.getChildLinksFromToken(getTokenName(), pct);

			boolean wroteContent = false;
			for (PCGraphEdge subEdge : subEdgeList)
			{
				if (subEdge.getPrerequisiteCount() != 1)
				{
					context
						.addWriteMessage("Only one Prerequisiste allowed on "
							+ getTokenName() + " derived edge");
					return null;
				}
				Prerequisite prereq = subEdge.getPrerequisiteList().get(0);
				String kind = prereq.getKind();
				if (kind.equalsIgnoreCase("LEVEL"))
				{
					if (!PrerequisiteOperator.GTEQ.equals(prereq.getOperator()))
					{
						context.addWriteMessage("Invalid Operator built on "
							+ getTokenName() + " derived edge");
						return null;
					}
				}
				else
				// if (!kind.equalsIgnoreCase("LEVEL"))
				{
					context.addWriteMessage("Prerequisiste on "
						+ getTokenName() + " derived edge must be LEVEL");
					return null;
				}
				List<PrereqObject> sinkNodes = subEdge.getSinkNodes();
				if (sinkNodes == null || sinkNodes.size() != 1)
				{
					context.addWriteMessage("Edge derived from "
						+ getTokenName() + " must have only one sink");
					return null;
				}
				PrereqObject sink = sinkNodes.get(0);
				if (!wroteContent)
				{
					if (sink instanceof DamageReduction)
					{
						sb.append("DR").append(sink);
					}
					else if (sink instanceof SpellResistance)
					{
						sb.append("SR").append(sink);
					}
					else if (sink instanceof SpecialAbility)
					{
						sb.append("SA").append(
							((SpecialAbility) sink).toLSTform());
					}
					else if (sink instanceof ChallengeRating)
					{
						sb.append("CR").append(
							((ChallengeRating) sink).toLSTform());
					}
					else
					{
						context.addWriteMessage("Cannot write "
							+ sink.getClass().getSimpleName() + " from "
							+ getTokenName());
						return null;
					}
					wroteContent = true;
				}
			}
			needsTab = true;
		}
		return sb.toString();
	}
}