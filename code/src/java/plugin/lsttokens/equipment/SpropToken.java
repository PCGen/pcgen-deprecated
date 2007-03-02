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
package plugin.lsttokens.equipment;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Equipment;
import pcgen.core.SpecialProperty;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

/**
 * Deals with SPROP token
 */
public class SpropToken extends AbstractToken implements EquipmentLstToken
{

	@Override
	public String getTokenName()
	{
		return "SPROP";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.addSpecialProperty(SpecialProperty.createFromLst(value));
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		if (value == null || value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + ": line minimally requires "
				+ getTokenName() + ":<text>");
			return false;
		}

		String firstToken = tok.nextToken();

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			context.graph.unlinkChildNodesOfClass(getTokenName(), eq,
				SpecialProperty.class);
			firstToken = tok.nextToken();
		}

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			Logging.errorPrint(getTokenName()
				+ " tag confused by redundant '.CLEAR'" + value);
			return false;
		}

		pcgen.cdom.content.SpecialProperty sa =
				new pcgen.cdom.content.SpecialProperty(firstToken);
		context.graph.linkObjectIntoGraph(getTokenName(), eq, sa);

		if (!tok.hasMoreTokens())
		{
			// No variables, we're done!
			return true;
		}

		String token = tok.nextToken();

		while (true)
		{
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				Logging.errorPrint(getTokenName()
					+ " tag confused by '.CLEAR' as a " + "middle token: "
					+ value);
				return false;
			}
			sa.addVariable(FormulaFactory.getFormulaFor(token));

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put items after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			sa.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		// if (obj instanceof PCClass) {
		// sa.setSASource("PCCLASS=" + obj.getKeyName() + "|" + level);
		// }

		return true;
	}

	public String unparse(LoadContext context, Equipment eq)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), eq,
					SpecialProperty.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		HashMapToList<Set<Prerequisite>, pcgen.cdom.content.SpecialProperty> m =
				new HashMapToList<Set<Prerequisite>, pcgen.cdom.content.SpecialProperty>();
		for (PCGraphEdge edge : edges)
		{
			pcgen.cdom.content.SpecialProperty sp =
					(pcgen.cdom.content.SpecialProperty) edge.getSinkNodes()
						.get(0);
			m.addToListFor(
				new HashSet<Prerequisite>(edge.getPrerequisiteList()), sp);
		}

		StringBuilder sb = new StringBuilder();
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		boolean needSpacer = false;
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			List<pcgen.cdom.content.SpecialProperty> props =
					m.getListFor(prereqs);
			if (needSpacer)
			{
				sb.append('\t');
			}
			sb.append(getTokenName()).append(':');
			boolean needBar = false;
			for (pcgen.cdom.content.SpecialProperty sp : props)
			{
				if (needBar)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(sp.getPropertyName());
				int variableCount = sp.getVariableCount();
				for (int i = 0; i < variableCount; i++)
				{
					sb.append(Constants.PIPE).append(sp.getVariable(i));
				}

			}
			if (prereqs != null && !prereqs.isEmpty())
			{
				for (Prerequisite p : prereqs)
				{
					StringWriter swriter = new StringWriter();
					try
					{
						prereqWriter.write(swriter, p);
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
							+ e);
						return null;
					}
					sb.append(Constants.PIPE).append(swriter.toString());
				}
			}
			needSpacer = true;
		}
		return sb.toString();
	}
}
