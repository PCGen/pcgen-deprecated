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
package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.Aggregator;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Language;
import pcgen.core.LanguageList;
import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken implements RaceLstToken
{
	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	public String getTokenName()
	{
		return "LANGBONUS";
	}

	public boolean parse(Race race, String value)
	{
		race.setLanguageBonus(value);
		return true;
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with ,| : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
		boolean foundAny = false;
		boolean foundOther = false;
		List<CDOMReference<Language>> list =
				new ArrayList<CDOMReference<Language>>();
		CDOMReference<LanguageList> swl =
				context.ref.getCDOMReference(LanguageList.class, "*Starting");
		Aggregator agg = new Aggregator(race, swl, getTokenName());

		PIPEWHILE: while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				context.graph.deleteAggregator(getTokenName(), agg);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Language> skill;
				String clearText = tokText.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					skill = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				}
				else
				{
					skill =
							TokenUtilities.getTypeOrPrimitive(context,
								LANGUAGE_CLASS, clearText);
				}
				if (skill == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
						+ getTokenName());
					return false;
				}
				Set<PCGraphEdge> edgeList =
						context.graph.getChildLinksFromToken(getTokenName(),
							agg, Aggregator.class);
				if (edgeList.size() != 1)
				{
					Logging.errorPrint("Internal Error: "
						+ "Expected only one " + getTokenName()
						+ " structure in Graph");
				}
				/*
				 * Note this can actually use agg and doesn't have to do a
				 * search from the edge in edgeList, due to the .equals property
				 * and how it will work in the Graph :) - Tom Parker Mar/1/07
				 */
				Set<PCGraphEdge> edgeToLanguageList =
						context.graph.getChildLinksFromToken(getTokenName(),
							agg, LANGUAGE_CLASS);
				for (PCGraphEdge se : edgeToLanguageList)
				{
					if (se.getNodeAt(1).equals(skill))
					{
						context.graph.unlinkChildNode(getTokenName(), agg,
							skill);
						Set<PCGraphEdge> links =
								context.graph.getChildLinksFromToken(
									getTokenName(), agg);
						if (links == null || links.isEmpty())
						{
							context.graph.deleteAggregator(getTokenName(), agg);
						}
						continue PIPEWHILE;
					}
				}
			}
			else
			{
				/*
				 * Note this is done one-by-one, because .CLEAR. token type
				 * needs to be able to perform the unlink. That could be
				 * changed, but the increase in complexity isn't worth it.
				 * (Changing it to a grouping object that didn't place links in
				 * the graph would also make it harder to trace the source of
				 * class skills, etc.)
				 */
				CDOMReference<Language> skill;
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					skill = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				}
				else
				{
					foundOther = true;
					skill =
							TokenUtilities.getTypeOrPrimitive(context,
								LANGUAGE_CLASS, tokText);
				}
				if (skill == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
						+ getTokenName());
					return false;
				}
				list.add(skill);
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		/*
		 * This is intentionally Holds, as the context for traversal must only
		 * be the ref (linked by the Activation Edge). So we need an edge to the
		 * Activator to get it copied into the PC, but since this is a 3rd party
		 * Token, the Race should never grant anything hung off the aggregator.
		 */
		context.graph.linkHoldsIntoGraph(getTokenName(), race, agg);
		context.graph.linkActivationIntoGraph(getTokenName(), swl, agg);

		for (CDOMReference<Language> prof : list)
		{
			context.graph.linkAllowIntoGraph(getTokenName(), agg, prof);
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), race,
					Aggregator.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		if (edgeList.size() != 1)
		{
			context.addWriteMessage("Expected only one " + getTokenName()
				+ " structure in Graph");
			return null;
		}
		PCGraphEdge edge = edgeList.iterator().next();
		Aggregator a = (Aggregator) edge.getNodeAt(1);
		Set<PCGraphEdge> edgeToLanguageList =
				context.graph.getChildLinksFromToken(getTokenName(), a,
					LANGUAGE_CLASS);
		SortedSet<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		for (PCGraphEdge se : edgeToLanguageList)
		{
			set.add((CDOMReference<Language>) se.getNodeAt(1));
		}
		return new String[]{ReferenceUtilities.joinLstFormat(set,
			Constants.COMMA)};
	}
}
