/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class LangautoLst implements GlobalLstToken
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	public String getTokenName()
	{
		return "LANGAUTO";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		final StringTokenizer tok = new StringTokenizer(value, ",");

		while (tok.hasMoreTokens())
		{
			obj.addLanguageAuto(tok.nextToken());
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		final StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				context.graph.unlinkChildNodesOfClass(getTokenName(), obj,
					LANGUAGE_CLASS);
			}
			else if (Constants.LST_ALL.equalsIgnoreCase(tokText))
			{
				context.graph.linkObjectIntoGraph(getTokenName(), obj,
					context.ref.getCDOMAllReference(LANGUAGE_CLASS));
			}
			else if (tokText.startsWith(Constants.LST_TYPE)
				|| tokText.startsWith(Constants.LST_TYPE_OLD))
			{
				String[] val = {tokText.substring(5)};
				context.graph.linkObjectIntoGraph(getTokenName(), obj,
					context.ref.getCDOMTypeReference(LANGUAGE_CLASS, val));
			}
			else
			{
				PrereqObject lang =
						context.ref.getCDOMReference(LANGUAGE_CLASS, tokText);
				context.graph.linkObjectIntoGraph(getTokenName(), obj, lang);
			}
		}
		return true;
	}

	public String unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					LANGUAGE_CLASS);
		if (edges.isEmpty())
		{
			return null;
		}
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append(':');
		boolean needComma = false;
		/*
		 * BUG Doesn't handle references correctly :(
		 */
		for (PCGraphEdge edge : edges)
		{
			if (needComma)
			{
				sb.append(Constants.COMMA);
			}
			sb.append(((Language) edge.getSinkNodes().get(0)).getKeyName());
			needComma = true;
		}
		return sb.toString();
	}
}
