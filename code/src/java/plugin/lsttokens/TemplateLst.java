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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.Aggregator;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Campaign;
import pcgen.core.PCTemplate;
import pcgen.core.PCTemplateList;
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

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
	{
		if (value.startsWith(Constants.LST_CHOOSE))
		{
			// TODO TCT is not enough to keep this unique across different
			// Templates
			CDOMReference<PCTemplateList> ref =
					context.ref.getCDOMReference(PCTemplateList.class, "*TCT");
			boolean returnval =
					parseChoose(context, cdo, ref, value
						.substring(Constants.LST_CHOOSE.length()));
			if (returnval)
			{
				context.graph.addSlotIntoGraph(getTokenName(), cdo,
					PCTEMPLATE_CLASS);
				// TODO Need to get the restriction attached to this slot...
			}
			return returnval;
		}
		else if (value.startsWith(Constants.LST_ADDCHOICE))
		{
			CDOMReference<PCTemplateList> ref =
					context.ref.getCDOMAllReference(PCTemplateList.class);
			return parseChoose(context, cdo, ref, value
				.substring(Constants.LST_ADDCHOICE.length()));
		}
		else
		{
			if (value.length() == 0)
			{
				Logging.errorPrint(getTokenName()
					+ " may not have empty argument");
				return false;
			}
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
				context.graph.linkObjectIntoGraph(getTokenName(), cdo, ref);
			}
		}

		return true;
	}

	public boolean parseChoose(LoadContext context, CDOMObject obj,
		CDOMReference<PCTemplateList> swl, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		List<CDOMReference<PCTemplate>> list =
				new ArrayList<CDOMReference<PCTemplate>>();

		while (tok.hasMoreTokens())
		{
			list.add(context.ref.getCDOMReference(PCTEMPLATE_CLASS, tok
				.nextToken()));
		}
		Aggregator agg = new Aggregator(obj, swl, getTokenName());
		/*
		 * This is intentionally Holds, as the context for traversal must only
		 * be the ref (linked by the Activation Edge). So we need an edge to the
		 * Activator to get it copied into the PC, but since this is a 3rd party
		 * Token, the Race should never grant anything hung off the aggregator.
		 */
		context.graph.linkHoldsIntoGraph(getTokenName(), obj, agg);
		context.graph.linkActivationIntoGraph(getTokenName(), swl, agg);

		for (CDOMReference<PCTemplate> prof : list)
		{
			context.graph.linkAllowIntoGraph(getTokenName(), agg, prof);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		Set<PCGraphEdge> directEdges =
				context.graph.getChildLinksFromToken(getTokenName(), cdo,
					PCTEMPLATE_CLASS);
		Set<PCGraphEdge> choiceEdges =
				context.graph.getChildLinksFromToken(getTokenName(), cdo,
					Aggregator.class);
		SortedSet<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		int currentIndex = 0;
		int choiceSize = choiceEdges == null ? 0 : choiceEdges.size();
		int directSize =
				directEdges == null ? 0 : directEdges.isEmpty() ? 0 : 1;
		if ((directSize + choiceSize) == 0)
		{
			// No templates
			return null;
		}
		String[] array = new String[directSize + choiceSize];
		if (directEdges != null && !directEdges.isEmpty())
		{
			for (PCGraphEdge edge : directEdges)
			{
				set.add((CDOMReference<?>) edge.getSinkNodes().get(0));
			}

			array[currentIndex++] =
					ReferenceUtilities.joinLstFormat(set, Constants.PIPE);
		}
		if (choiceEdges != null && !choiceEdges.isEmpty())
		{
			for (PCGraphEdge edge : choiceEdges)
			{
				Aggregator a = (Aggregator) edge.getNodeAt(1);
				Set<PCGraphEdge> edgeToTemplateList =
						context.graph.getParentLinksFromToken(getTokenName(),
							a, PCTemplateList.class);
				Set<PCGraphEdge> edgeToChildList =
						context.graph.getChildLinksFromToken(getTokenName(), a,
							PCTEMPLATE_CLASS);
				CDOMReference<PCTemplateList> parent =
						(CDOMReference<PCTemplateList>) edgeToTemplateList
							.iterator().next().getNodeAt(0);
				set.clear();
				for (PCGraphEdge se : edgeToChildList)
				{
					set.add((CDOMReference<PCTemplate>) se.getNodeAt(1));
				}
				String prefix =
						parent.getLSTformat().equals("ALL")
							? Constants.LST_ADDCHOICE : Constants.LST_CHOOSE;
				array[currentIndex++] =
						(prefix + ReferenceUtilities.joinLstFormat(set,
							Constants.PIPE));
			}
		}
		return array;
	}
}
