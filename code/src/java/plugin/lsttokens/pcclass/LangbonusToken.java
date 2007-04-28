/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken implements PCClassLstToken, PCClassClassLstToken
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	public String getTokenName()
	{
		return "LANGBONUS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setLanguageBonus(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
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
				+ " arguments may not end with , : " + value);
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		final StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
		List<CDOMReference<Language>> list =
				new ArrayList<CDOMReference<Language>>();

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();

			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				context.graph.unlinkChildNodesOfClass(getTokenName(), pcc,
					LANGUAGE_CLASS);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Language> ref;
				if (Constants.LST_ALL.equals(tokText))
				{
					ref = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				}
				else
				{
					ref =
							TokenUtilities.getTypeOrPrimitive(context,
								LANGUAGE_CLASS, tokText.substring(7));
				}
				if (ref == null)
				{
					return false;
				}
				context.graph.unlinkChildNode(getTokenName(), pcc, ref);
			}
			else
			{
				/*
				 * Note this HAS to be added one-by-one, because the
				 * .unlinkChildNodesOfClass method above does NOT recognize the
				 * Language object and therefore doesn't know how to search the
				 * sublists
				 */
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					list.add(context.ref.getCDOMAllReference(LANGUAGE_CLASS));
				}
				else
				{
					foundOther = true;
					CDOMReference<Language> ref =
							TokenUtilities.getTypeOrPrimitive(context,
								LANGUAGE_CLASS, tokText);
					if (ref == null)
					{
						return false;
					}
					list.add(ref);
				}
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		for (CDOMReference<Language> ref : list)
		{
			/*
			 * BUG FIXME This is NOT A GRANT - it is a ChoiceList like
			 * WeaponBonus
			 */
			context.graph.linkObjectIntoGraph(getTokenName(), pcc, ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), pcc,
					LANGUAGE_CLASS);
		if (edges.isEmpty())
		{
			return null;
		}
		SortedSet<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		for (PCGraphEdge edge : edges)
		{
			set.add((CDOMReference<Language>) edge.getSinkNodes().get(0));
		}
		return new String[]{ReferenceUtilities.joinLstFormat(set,
			Constants.COMMA)};
	}
}
