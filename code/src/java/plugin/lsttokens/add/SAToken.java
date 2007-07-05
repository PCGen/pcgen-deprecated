/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.add;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.factory.GrantFactory;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.SpecialAbility;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.AddLstToken;
import pcgen.util.Logging;

public class SAToken extends AbstractToken implements AddLstToken
{

	private static final Class<SpecialAbility> SPECABILITY_CLASS =
			SpecialAbility.class;

	public boolean parse(PObject target, String value, int level)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.deprecationPrint("Lack of a SUBTOKEN for ADD:SA "
				+ "is prohibited in new syntax.");
			Logging.deprecationPrint("Please use ADD:SA|name|[count|]X,X");
			return false;
		}
		String subToken = value.substring(0, pipeLoc);
		String countString;
		String items;
		int lastPipeLoc = value.lastIndexOf(Constants.PIPE);
		if (lastPipeLoc == pipeLoc)
		{
			items = value;
			countString = "1";
		}
		else
		{
			items = value.substring(pipeLoc + 1, lastPipeLoc);
			countString = value.substring(lastPipeLoc + 1);
		}
		target.addAddList(level, subToken + "(" + items + ")" + countString);
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "SA";
	}

	public boolean parse(LoadContext context, PObject obj, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint("Lack of a SUBTOKEN for ADD:SA "
				+ "is prohibited.");
			Logging.errorPrint("Please use ADD:SA|name|[count|]X,X");
			return false;
		}
		String name = value.substring(0, pipeLoc);
		if (name.length() == 0)
		{
			Logging.errorPrint("Empty name for ADD:SA " + "is prohibited.");
			Logging.errorPrint("Please use ADD:SA|name|[count|]X,X");
			return false;
		}
		String rest = value.substring(pipeLoc + 1);
		int count;
		String items;
		pipeLoc = rest.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			count = 1;
			items = rest;
		}
		else
		{
			String countString = rest.substring(0, pipeLoc);
			try
			{
				count = Integer.parseInt(countString);
				if (count < 1)
				{
					Logging.errorPrint("Count in ADD:" + getTokenName()
						+ " must be > 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid Count in ADD:" + getTokenName()
					+ ": " + countString);
				return false;
			}
			items = rest.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}

		List<CDOMReference<SpecialAbility>> refs =
				new ArrayList<CDOMReference<SpecialAbility>>();
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			context.ref.constructIfNecessary(SPECABILITY_CLASS, token);
			refs.add(context.ref.getCDOMReference(SPECABILITY_CLASS, token));
		}
		ReferenceChoiceSet<SpecialAbility> rcs =
				new ReferenceChoiceSet<SpecialAbility>(refs);
		ChoiceSet<SpecialAbility> cs = new ChoiceSet<SpecialAbility>(name, rcs);
		PCGraphGrantsEdge edge = context.graph.grant(getTokenName(), obj, cs);
		edge.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(count));
		edge.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE));
		GrantFactory<SpecialAbility> gf =
				new GrantFactory<SpecialAbility>(edge);
		context.graph.grant(getTokenName(), obj, gf);
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		GraphChanges<ChoiceSet> changes =
				context.graph.getChangesFromToken(getTokenName(), obj,
					ChoiceSet.class);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		TreeSet<String> addStrings = new TreeSet<String>();
		for (LSTWriteable lstw : added)
		{
			ChoiceSet<?> cs = (ChoiceSet<?>) lstw;
			if (SPECABILITY_CLASS.equals(cs.getChoiceClass()))
			{
				AssociatedPrereqObject assoc =
						changes.getAddedAssociation(lstw);
				Formula f = assoc.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					// Error
					return null;
				}
				StringBuilder sb = new StringBuilder();
				sb.append(cs.getName()).append(Constants.PIPE);
				String fString = f.toString();
				if (!"1".equals(fString))
				{
					sb.append(fString).append(Constants.PIPE);
				}
				sb.append(cs.getLSTformat());
				addStrings.add(sb.toString());
				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}
}
