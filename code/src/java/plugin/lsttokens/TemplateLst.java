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
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.ChoiceSet;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Campaign;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class TemplateLst implements GlobalLstToken
{

	private static final Class<PCTemplate> PCTEMPLATE_CLASS = PCTemplate.class;

	public String getTokenName()
	{
		return "TEMPLATE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!(obj instanceof Campaign))
		{
			obj.addTemplate(value);
			return true;
		}
		return false;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (value.startsWith(Constants.LST_CHOOSE))
		{
			return parseChoose(context, obj, value);
		}
		else if (value.startsWith(Constants.LST_ADDCHOICE))
		{
			return parseAddChoice(context, obj, value);
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				context.graph.unlinkChildNodesOfClass(getTokenName(), obj,
					PCTEMPLATE_CLASS);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				PrereqObject pct =
						context.ref.getCDOMReference(PCTEMPLATE_CLASS, tokText
							.substring(7));
				context.graph.unlinkChildNode(getTokenName(), obj, pct);
			}
			else
			{
				context.graph.linkObjectIntoGraph(getTokenName(), obj,
					context.ref.getCDOMReference(PCTEMPLATE_CLASS, tokText));
			}
		}
		return true;
	}

	private boolean parseChoose(LoadContext context, CDOMObject obj,
		String value)
	{
		StringTokenizer tok =
				new StringTokenizer(value.substring(7), Constants.PIPE);
		ChoiceSet<CDOMSimpleSingleRef<PCTemplate>> cl =
				new ChoiceSet<CDOMSimpleSingleRef<PCTemplate>>(1, tok
					.countTokens());
		while (tok.hasMoreTokens())
		{
			cl.addChoice(context.ref.getCDOMReference(PCTEMPLATE_CLASS, tok
				.nextToken()));
		}
		context.graph.linkObjectIntoGraph(getTokenName(), obj, cl);
		return true;
	}

	private boolean parseAddChoice(LoadContext context, CDOMObject obj,
		String value)
	{
		Set<PCGraphEdge> edgeSet =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					ChoiceSet.class);
		if (edgeSet.size() != 1)
		{
			Logging.errorPrint(getTokenName()
				+ ":ADDCHOICE cannot be performed because more than "
				+ "one ChoiceList is attached to "
				+ obj.getClass().getSimpleName() + " " + obj.getKeyName());
			return false;
		}
		PCGraphEdge edge = edgeSet.iterator().next();
		StringTokenizer tok =
				new StringTokenizer(value.substring(7), Constants.PIPE);
		ChoiceSet<CDOMSimpleSingleRef<PCTemplate>> cl =
				(ChoiceSet<CDOMSimpleSingleRef<PCTemplate>>) edge.getNodeAt(1);
		cl.addChoice(context.ref.getCDOMReference(PCTEMPLATE_CLASS, tok
			.nextToken()));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> choiceEdgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					ChoiceSet.class);
		Set<PCGraphEdge> templateEdgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					PCTemplate.class);
		int arrayLength = choiceEdgeList.isEmpty() ? 0 : 1;
		arrayLength += templateEdgeList.isEmpty() ? 0 : 1;
		if (arrayLength == 0)
		{
			return null;
		}
		String[] array = new String[arrayLength];
		int index = 0;
		if (!choiceEdgeList.isEmpty())
		{
			if (choiceEdgeList.size() > 1)
			{
				context.addWriteMessage("Not valid to have more than one "
					+ "ChoiceList created by " + getTokenName());
				return null;
			}
			ChoiceSet<CDOMSimpleSingleRef<PCTemplate>> cl =
					(ChoiceSet<CDOMSimpleSingleRef<PCTemplate>>) choiceEdgeList
						.iterator().next().getSinkNodes().get(0);
			array[index++] =
					ReferenceUtilities.joinLstFormat(cl.getSet(),
						Constants.PIPE);
		}
		if (!templateEdgeList.isEmpty())
		{
			Set<CDOMReference<?>> set =
					new TreeSet<CDOMReference<?>>(
						TokenUtilities.REFERENCE_SORTER);
			for (PCGraphEdge edge : templateEdgeList)
			{
				CDOMReference<?> pct =
						(CDOMReference<?>) edge.getSinkNodes().get(0);
				set.add(pct);
			}
			array[index++] =
					ReferenceUtilities.joinLstFormat(set, Constants.PIPE);
		}
		return array;
	}
}
