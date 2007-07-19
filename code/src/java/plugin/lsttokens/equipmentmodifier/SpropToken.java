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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.SpecialProperty;
import pcgen.core.EquipmentModifier;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EquipmentModifierLstToken;
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
		context.graph.grant(getTokenName(), mod, sa);
		return true;
	}

	public SpecialProperty subParse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return null;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String firstToken = tok.nextToken();

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			context.graph.removeAll(getTokenName(), mod);
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
		GraphChanges<SpecialProperty> changes =
				context.graph.getChangesFromToken(getTokenName(), mod,
					SpecialProperty.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (LSTWriteable ab : added)
		{
			SpecialProperty sp = (SpecialProperty) ab;
			StringBuilder sb = new StringBuilder();
			sb.append(sp.getPropertyName());
			int variableCount = sp.getVariableCount();
			for (int i = 0; i < variableCount; i++)
			{
				sb.append(Constants.PIPE).append(sp.getVariable(i));
			}
			if (sp.hasPrerequisites())
			{
				sb.append(Constants.PIPE);
				sb.append(getPrerequisiteString(context, sp.getPrerequisiteList()));
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}
}
