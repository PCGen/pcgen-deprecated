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
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken implements PCTemplateLstToken
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	public String getTokenName()
	{
		return "LANGBONUS";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setLanguageBonus(value);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		if (value.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}
		final StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();

			if (Constants.LST_CLEAR.equals(tokText))
			{
				context.graph.unlinkChildNodesOfClass(getTokenName(), template,
					LANGUAGE_CLASS);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Language> lang =
						TokenUtilities.getObjectReference(context,
							LANGUAGE_CLASS, tokText.substring(7));
				if (lang == null)
				{
					return false;
				}
				context.graph.unlinkChildNode(getTokenName(), template, lang);
			}
			else
			{
				/*
				 * Note this HAS to be added one-by-one, because the
				 * .unlinkChildNodesOfClass method above does NOT recognize the
				 * Language object and therefore doesn't know how to search the
				 * sublists
				 */
				CDOMReference<Language> lang =
						TokenUtilities.getObjectReference(context,
							LANGUAGE_CLASS, tokText);
				if (lang == null)
				{
					return false;
				}
				context.graph.linkObjectIntoGraph(getTokenName(), template,
					lang);
			}
		}
		return true;
	}

	public String unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					LANGUAGE_CLASS);
		if (edges.isEmpty())
		{
			return null;
		}
		SortedSet<CDOMReference<Language>> set =
				new TreeSet<CDOMReference<Language>>(
					TokenUtilities.REFERENCE_SORTER);
		boolean needComma = false;
		for (PCGraphEdge edge : edges)
		{
			set.add((CDOMReference<Language>) edge.getSinkNodes().get(0));
		}
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append(':');
		for (CDOMReference<Language> ref : set)
		{
			if (needComma)
			{
				sb.append(Constants.COMMA);
			}
			needComma = true;
			sb.append(ref.getLSTformat());
		}
		return sb.toString();
	}
}
