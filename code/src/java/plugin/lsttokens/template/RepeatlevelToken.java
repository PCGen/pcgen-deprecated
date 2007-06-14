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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.Aggregator;
import pcgen.core.PCTemplate;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.LstUtils;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.persistence.lst.TokenStore;
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
		if (endRepeat <= 0)
		{
			Logging.errorPrint("Invalid " + getTokenName() + " (no colon) : "
				+ value);
			return false;
		}
		final int endLevel = value.indexOf(':', endRepeat + 1);
		if (endLevel <= 0)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ " (only one colon) : " + value);
			return false;
		}
		String repeatSec = value.substring(0, endRepeat);
		final StringTokenizer repeatToken = new StringTokenizer(repeatSec, "|");
		if (repeatToken.countTokens() != 3)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ " (repeat section " + repeatSec
				+ " does not have two pipes) : " + value);
			return false;
		}
		try
		{
			final int lvlIncrement = Integer.parseInt(repeatToken.nextToken());
			if (lvlIncrement <= 0)
			{
				Logging.errorPrint("Invalid level increment in "
					+ getTokenName() + ": must be > 0 : " + value);
				return false;
			}
			final int consecutive = Integer.parseInt(repeatToken.nextToken());
			if (consecutive < 0)
			{
				Logging.errorPrint("Invalid consecutive setting in "
					+ getTokenName() + ": must be >= 0 : " + value);
				return false;
			}
			final int maxLevel = Integer.parseInt(repeatToken.nextToken());
			if (maxLevel <= 0)
			{
				Logging.errorPrint("Invalid max level in " + getTokenName()
					+ ": must be > 0 : " + value);
				return false;
			}
			int iLevel =
					Integer.parseInt(value.substring(endRepeat + 1, endLevel));
			if (iLevel <= 0)
			{
				Logging.errorPrint("Invalid start level in " + getTokenName()
					+ ": must be > 0 : " + value);
				return false;
			}

			int count = consecutive;
			for (; iLevel <= maxLevel; iLevel += lvlIncrement)
			{
				if ((consecutive == 0) || (count != 0))
				{
					final StringTokenizer tok =
							new StringTokenizer(value.substring(endLevel + 1));
					final String type = tok.nextToken();

					template.addLevelAbility(iLevel, type, tok.nextToken());
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
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": number error encountered in :" + value);
			return false;
		}
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
		throws PersistenceLayerException
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
		if (consecutive < 0)
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

		if (iLevel > maxLevel)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Starting Level was > Maximum Level)");
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (iLevel + lvlIncrement > maxLevel)
		{
			Logging
				.errorPrint("Malformed "
					+ getTokenName()
					+ " Token (Does not repeat, Staring Level + Increment > Maximum Level)");
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (consecutive != 0
			&& ((maxLevel - iLevel) / lvlIncrement) < consecutive)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (Does not use Skip Interval value): " + consecutive);
			Logging.errorPrint("  You should set the interval to zero");
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String typeStr = value.substring(endLevel + 1, endAssignType);
		String contentStr = value.substring(endAssignType + 1);
		if (contentStr.length() == 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
				+ " Token (No Content to SubToken)");
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		Aggregator agg = new Aggregator(template, template, getTokenName());
		agg.put(IntegerKey.CONSECUTIVE, Integer.valueOf(consecutive));
		agg.put(IntegerKey.MAX_LEVEL, Integer.valueOf(maxLevel));
		agg.put(IntegerKey.LEVEL_INCREMENT, Integer.valueOf(lvlIncrement));
		agg.put(IntegerKey.START_LEVEL, Integer.valueOf(iLevel));

		context.graph.grant(getTokenName(), template, agg);

		for (int count = consecutive; iLevel <= maxLevel; iLevel +=
				lvlIncrement)
		{
			if ((consecutive == 0) || (count != 0))
			{
				/*
				 * TODO There is DEFINITELY an overlap problem here due to
				 * equality
				 */
				PCTemplate derivative = template.getPseudoTemplate();
				derivative.put(ObjectKey.PSEUDO_PARENT, template);
				derivative
					.addPrerequisite(getPrerequisite("PRELEVEL:" + iLevel));
				context.graph.grant(getTokenName(), agg, derivative);
				PCTemplateLstToken token =
						TokenStore.inst().getToken(PCTemplateLstToken.class,
							typeStr);
				if (token == null)
				{
					if (!PObjectLoader.parseTag(context, derivative, typeStr,
						contentStr))
					{
						Logging.errorPrint("Illegal template Token '" + typeStr
							+ "' '" + value + "' for "
							+ template.getDisplayName());
						return false;
					}
				}
				else
				{
					LstUtils.deprecationCheck(token, derivative, contentStr);
					if (!token.parse(context, derivative, contentStr))
					{
						Logging.errorPrint("Error Parsing Token '" + typeStr
							+ " in template " + template.getDisplayName());
						return false;
					}
				}
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

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					Aggregator.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		TreeSet<Aggregator> aggSet =
				new TreeSet<Aggregator>(TokenUtilities.AGG_COMPARATOR);
		for (PCGraphEdge edge : edgeList)
		{
			aggSet.add((Aggregator) edge.getSinkNodes().get(0));
		}
		List<String> list = new ArrayList<String>(aggSet.size());
		for (Aggregator agg : aggSet)
		{
			StringBuilder sb = new StringBuilder();
			Integer consecutive = agg.get(IntegerKey.CONSECUTIVE);
			Integer maxLevel = agg.get(IntegerKey.MAX_LEVEL);
			Integer lvlIncrement = agg.get(IntegerKey.LEVEL_INCREMENT);
			Integer iLevel = agg.get(IntegerKey.START_LEVEL);
			sb.append(lvlIncrement).append(Constants.PIPE);
			sb.append(consecutive).append(Constants.PIPE);
			sb.append(maxLevel).append(Constants.COLON);
			sb.append(iLevel).append(Constants.COLON);
			String prefix = sb.toString();
			Set<PCGraphEdge> subEdgeList =
					context.graph.getChildLinksFromToken(getTokenName(), agg);
			if (subEdgeList == null || subEdgeList.isEmpty())
			{
				context.addWriteMessage("Aggregator for " + getTokenName()
					+ " had no children");
				return null;
			}

			Set<String> set = new TreeSet<String>();

			for (PCGraphEdge edge : subEdgeList)
			{
				List<PrereqObject> sinkNodes = edge.getSinkNodes();
				if (sinkNodes == null || sinkNodes.size() != 1)
				{
					context.addWriteMessage("Edge derived from "
						+ getTokenName() + " must have only one sink");
					return null;
				}
				PrereqObject child = sinkNodes.get(0);
				if (!PCTemplate.class.isInstance(child))
				{
					context.addWriteMessage("Child from " + getTokenName()
						+ " must be a PCTemplate");
					return null;
				}
				PCTemplate pctChild = PCTemplate.class.cast(child);
				if (pctChild.getPrerequisiteCount() != 1)
				{
					context
						.addWriteMessage("Only one Prerequisiste allowed on "
							+ getTokenName() + " child PCTemplate");
					return null;
				}
				Prerequisite prereq = pctChild.getPrerequisiteList().get(0);
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
				{
					// if (!kind.equalsIgnoreCase("LEVEL"))
					context.addWriteMessage("Prerequisiste on "
						+ getTokenName() + " derived edge must be LEVEL");
					return null;
				}
				if (!PrerequisiteOperator.GTEQ.equals(prereq.getOperator()))
				{
					context.addWriteMessage("Invalid Operator built on "
						+ getTokenName() + " derived edge");
					return null;
				}

				for (LstToken token : TokenStore.inst().getTokenMap(
					PCTemplateLstToken.class).values())
				{
					StringBuilder sb2 = new StringBuilder();
					sb2.append(token.getTokenName()).append(':');
					String[] s =
							((PCTemplateLstToken) token).unparse(context,
								pctChild);
					if (s != null)
					{
						for (String aString : s)
						{
							set.add(sb2.toString() + aString);
						}
					}
				}
				for (LstToken token : TokenStore.inst().getTokenMap(
					GlobalLstToken.class).values())
				{
					StringBuilder sb2 = new StringBuilder();
					sb2.append(token.getTokenName()).append(':');
					String[] s =
							((GlobalLstToken) token).unparse(context, pctChild);
					if (s != null)
					{
						for (String aString : s)
						{
							set.add(sb2.toString() + aString);
						}
					}
				}
			}

			for (String s : set)
			{
				list.add(prefix + s);
			}
		}
		return list.toArray(new String[list.size()]);
	}
}