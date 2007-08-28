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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCTemplate;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.LstUtils;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.persistence.lst.TokenStore;
import pcgen.util.Logging;

/**
 * Class deals with REPEATLEVEL Token
 */
public class RepeatlevelToken extends AbstractToken implements
		PCTemplateLstToken
{

	private static final Class<PCTemplate> PCTEMPLATE_CLASS = PCTemplate.class;

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

		String pseudoName =
				new StringBuilder(consecutive).append(':').append(maxLevel)
					.append(':').append(lvlIncrement).append(':')
					.append(iLevel).toString();
		PCTemplate consolidator = template.getPseudoTemplate(pseudoName);
		consolidator.put(IntegerKey.CONSECUTIVE, Integer.valueOf(consecutive));
		consolidator.put(IntegerKey.MAX_LEVEL, Integer.valueOf(maxLevel));
		consolidator.put(IntegerKey.LEVEL_INCREMENT, Integer
			.valueOf(lvlIncrement));
		consolidator.put(IntegerKey.START_LEVEL, Integer.valueOf(iLevel));
		consolidator.put(StringKey.TOKEN, getTokenName());

		context.getGraphContext().grant(getTokenName(), template, consolidator);

		for (int count = consecutive; iLevel <= maxLevel; iLevel +=
				lvlIncrement)
		{
			if ((consecutive == 0) || (count != 0))
			{
				Prerequisite prereq = getPrerequisite("PRELEVEL:" + iLevel);
				String standardizedPrereq =
						getPrerequisiteString(context, Collections
							.singletonList(prereq));
				PCTemplate derivative =
						consolidator.getPseudoTemplate(standardizedPrereq);
				derivative.put(ObjectKey.PSEUDO_PARENT, consolidator);
				derivative.addPrerequisite(prereq);
				context.getGraphContext().grant(getTokenName(), consolidator,
					derivative);
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
		AssociatedChanges<PCTemplate> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					pct, PCTEMPLATE_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		Set<String> list = new TreeSet<String>();
		for (LSTWriteable lstw : new HashSet<LSTWriteable>(added))
		{
			PCTemplate agg = PCTEMPLATE_CLASS.cast(lstw);
			if (!getTokenName().equals(agg.get(StringKey.TOKEN)))
			{
				continue;
			}
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
			AssociatedChanges<PCTemplate> subchanges =
					context.getGraphContext().getChangesFromToken(
						getTokenName(), agg, PCTEMPLATE_CLASS);
			Collection<LSTWriteable> perAddCollection = subchanges.getAdded();
			if (perAddCollection == null || perAddCollection.isEmpty())
			{
				context.addWriteMessage("Invalid Consolidator built in "
					+ getTokenName() + ": had no subTemplates");
				return null;
			}
			LSTWriteable next = perAddCollection.iterator().next();
			Set<String> set = subUnparse(prefix, context, (PCTemplate) next);
			list.addAll(set);
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	private Set<String> subUnparse(String prefix, LoadContext context,
		PCTemplate agg)
	{
		Set<String> set = new TreeSet<String>();
		for (LstToken token : TokenStore.inst().getTokenMap(
			PCTemplateLstToken.class).values())
		{
			String[] s = ((PCTemplateLstToken) token).unparse(context, agg);
			if (s != null)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(prefix).append(token.getTokenName()).append(':');
				String fullPrefix = sb.toString();
				for (String aString : s)
				{
					set.add(fullPrefix + aString);
				}
			}
		}
		for (LstToken token : TokenStore.inst().getTokenMap(
			GlobalLstToken.class).values())
		{
			String[] s = ((GlobalLstToken) token).unparse(context, agg);
			if (s != null)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(prefix).append(token.getTokenName()).append(':');
				String fullPrefix = sb.toString();
				for (String aString : s)
				{
					set.add(fullPrefix + aString);
				}
			}
		}
		return set;
	}
}