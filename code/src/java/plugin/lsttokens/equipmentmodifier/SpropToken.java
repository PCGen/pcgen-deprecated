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
package plugin.lsttokens.equipmentmodifier;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.SpecialProperty;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.EquipmentModifier;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

/**
 * Deals with SPROP token
 */
public class SpropToken extends AbstractToken implements
		EquipmentModifierLstToken
{

	@Override
	public String getTokenName()
	{
		return "SPROP";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.addSpecialProperty(pcgen.core.SpecialProperty.createFromLst(value));
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		SpecialProperty sa = subParse(context, mod, value);
		if (sa == null)
		{
			return false;
		}
		context.graph.linkObjectIntoGraph(getTokenName(), mod, sa);
		return true;
	}
	
	public SpecialProperty subParse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		if (value == null || value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + ": line minimally requires "
				+ getTokenName() + ":<text>");
			return null;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return null;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return null;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return null;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String firstToken = tok.nextToken();

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			context.graph.unlinkChildNodesOfClass(getTokenName(), mod,
				SpecialProperty.class);
			firstToken = tok.nextToken();
		}

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			Logging.errorPrint(getTokenName()
				+ " tag confused by redundant '.CLEAR'" + value);
			return null;
		}

		if (firstToken.startsWith("PRE") || firstToken.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
				+ getTokenName());
			return null;
		}

		SpecialProperty sa = new SpecialProperty(firstToken);

		if (!tok.hasMoreTokens())
		{
			// No variables, we're done!
			return sa;
		}

		String token = tok.nextToken();

		while (true)
		{
			/*
			 * FIXME This is the ONLY Token fixed so far for a leading pre:
			 * Yarra Valley|PRELEVEL:4|Rheinhessen
			 * 
			 * This check needs to be universal in all the tokens that do this
			 * trailing PRE check
			 */
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				Logging.errorPrint(getTokenName()
					+ " tag confused by '.CLEAR' as a " + "middle token: "
					+ value);
				return null;
			}
			sa.addVariable(FormulaFactory.getFormulaFor(token));

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return sa;
			}
			token = tok.nextToken();
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put items after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return null;
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

		return sa;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), mod,
					SpecialProperty.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		List<String> list = new ArrayList<String>();
		for (PCGraphEdge edge : edges)
		{
			SpecialProperty sp = (SpecialProperty) edge.getSinkNodes().get(0);
			StringBuilder sb = new StringBuilder();
			sb.append(sp.getPropertyName());
			int variableCount = sp.getVariableCount();
			for (int i = 0; i < variableCount; i++)
			{
				sb.append(Constants.PIPE).append(sp.getVariable(i));
			}
			List<Prerequisite> prereqs = sp.getPrerequisiteList();
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
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}
}
