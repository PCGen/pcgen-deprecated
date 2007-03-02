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
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ChoiceSet;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with TEMPLATE Token
 */
public class TemplateToken implements PCTemplateLstToken
{

	private static final Class<PCTemplate> PCTEMPLATE_CLASS = PCTemplate.class;

	public String getTokenName()
	{
		return "TEMPLATE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.addTemplate(value);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		if (value.startsWith(Constants.LST_CHOOSE))
		{
			String substring = value.substring(Constants.LST_CHOOSE.length());
			if (substring.length() == 0)
			{
				Logging.errorPrint("Invalid " + getTokenName() + ":"
					+ Constants.LST_CHOOSE);
				Logging.errorPrint("  Requires at least one argument");
				return false;
			}
			if (substring.charAt(0) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not start with | , see: " + value);
				return false;
			}
			if (substring.charAt(substring.length() - 1) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not end with | , see: " + value);
				return false;
			}
			if (substring.indexOf("||") != -1)
			{
				Logging.errorPrint(getTokenName()
					+ " arguments uses double separator || : " + value);
				return false;
			}

			StringTokenizer tok =
					new StringTokenizer(substring, Constants.PIPE);

			ChoiceSet<CDOMSimpleSingleRef<PCTemplate>> cl =
					new ChoiceSet<CDOMSimpleSingleRef<PCTemplate>>(1, tok
						.countTokens());

			while (tok.hasMoreTokens())
			{
				String tokText = tok.nextToken();
				CDOMSimpleSingleRef<PCTemplate> ref =
						context.ref.getCDOMReference(PCTEMPLATE_CLASS, tokText);
				cl.addChoice(ref);
			}
			context.graph.linkObjectIntoGraph(getTokenName(), template, cl);
		}
		else if (value.startsWith(Constants.LST_ADDCHOICE))
		{
			String substring =
					value.substring(Constants.LST_ADDCHOICE.length());
			if (substring.length() == 0)
			{
				Logging.errorPrint("Invalid " + getTokenName() + ":"
					+ Constants.LST_CHOOSE);
				Logging.errorPrint("  Requires at least one argument");
				return false;
			}

			// FIXME Need to handle this :)
			/*
			 * The disappointing thing here is that this produces interaction
			 * with my idea about .MODs and .CLEARs, since this is an implicit
			 * .MOD, but .MODs something that the context will not be aware is
			 * being modified :/
			 */
		}
		else
		{
			if (value.charAt(0) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not start with | , see: " + value);
				return false;
			}
			if (value.charAt(value.length() - 1) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not end with | , see: " + value);
				return false;
			}
			if (value.indexOf("||") != -1)
			{
				Logging.errorPrint(getTokenName()
					+ " arguments uses double separator || : " + value);
				return false;
			}

			StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

			while (tok.hasMoreTokens())
			{
				String tokText = tok.nextToken();
				CDOMSimpleSingleRef<PCTemplate> ref =
						context.ref.getCDOMReference(PCTEMPLATE_CLASS, tokText);
				context.graph
					.linkObjectIntoGraph(getTokenName(), template, ref);
			}
		}

		return true;
	}

	public String unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> directEdges =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					PCTEMPLATE_CLASS);
		Set<PCGraphEdge> choiceEdges =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					ChoiceSet.class);
		if (choiceEdges == null || choiceEdges.isEmpty())
		{
			if (directEdges == null || directEdges.isEmpty())
			{
				return null;
			}
			return unparseDirect(context, pct, directEdges).toString();
		}
		else if (directEdges == null || directEdges.isEmpty())
		{
			return unparseChoice(context, pct, choiceEdges).toString();
		}
		return unparseDirect(context, pct, directEdges).append('\t').append(
			unparseChoice(context, pct, choiceEdges)).toString();
	}

	private StringBuilder unparseDirect(LoadContext context, PCTemplate pct,
		Set<PCGraphEdge> directEdges)
	{
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append(':');
		SortedSet<CDOMReference<PCTemplate>> set =
				new TreeSet<CDOMReference<PCTemplate>>(
					TokenUtilities.REFERENCE_SORTER);
		for (PCGraphEdge edge : directEdges)
		{
			set.add((CDOMReference<PCTemplate>) edge.getSinkNodes().get(0));
		}
		boolean needPipe = false;
		for (CDOMReference<PCTemplate> ref : set)
		{
			if (needPipe)
			{
				sb.append(Constants.PIPE);
			}
			needPipe = true;
			sb.append(ref.getLSTformat());
		}
		return sb;
	}

	private StringBuilder unparseChoice(LoadContext context, PCTemplate pct,
		Set<PCGraphEdge> directEdges)
	{
		StringBuilder sb = new StringBuilder();
		for (PCGraphEdge edge : directEdges)
		{
			sb.append(getTokenName()).append(':').append(Constants.LST_CHOOSE);
			ChoiceSet<CDOMSimpleSingleRef<PCTemplate>> cl =
					(ChoiceSet<CDOMSimpleSingleRef<PCTemplate>>) edge
						.getSinkNodes().get(0);
			Set<CDOMSimpleSingleRef<PCTemplate>> items = cl.getSet();

			SortedSet<CDOMReference<PCTemplate>> set =
					new TreeSet<CDOMReference<PCTemplate>>(
						TokenUtilities.REFERENCE_SORTER);
			set.addAll(items);
			boolean needPipe = false;
			for (CDOMReference<PCTemplate> ref : set)
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				needPipe = true;
				sb.append(ref.getLSTformat());
			}
		}
		return sb;
	}
}
