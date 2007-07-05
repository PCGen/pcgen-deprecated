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
package plugin.lsttokens.remove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.factory.RemoveFactory;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.CompoundOrChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.Ability;
import pcgen.core.PObject;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.RemoveLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class FeatToken extends AbstractToken implements RemoveLstToken
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	public boolean parse(PObject target, String value, int level)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		String countString;
		String items;
		if (pipeLoc == -1)
		{
			countString = "1";
			items = value;
		}
		else
		{
			if (pipeLoc != value.lastIndexOf(Constants.PIPE))
			{
				Logging.errorPrint("Syntax of REMOVE:" + getTokenName()
					+ " only allows one | : " + value);
				return false;
			}
			countString = value.substring(0, pipeLoc);
			items = value.substring(pipeLoc + 1);
		}
		if (level > -9)
		{
			target.setRemoveString(level + "|" + getTokenName() + "(" + items
				+ ")" + countString);
		}
		else
		{
			target.setRemoveString("0|" + getTokenName() + "(" + items + ")"
				+ countString);
		}
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(LoadContext context, PObject obj, String value)
		throws PersistenceLayerException
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		int count;
		String items;
		if (pipeLoc == -1)
		{
			count = 1;
			items = value;
		}
		else
		{
			String countString = value.substring(0, pipeLoc);
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
			items = value.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);

		List<CDOMReference<Ability>> refs =
				new ArrayList<CDOMReference<Ability>>();
		List<PrimitiveChoiceSet<Ability>> pcsList =
				new ArrayList<PrimitiveChoiceSet<Ability>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				CDOMReference<Ability> ref =
						context.ref.getCDOMAllReference(ABILITY_CLASS,
							AbilityCategory.FEAT);
				refs.add(ref);
			}
			else if ("CHOICE".equalsIgnoreCase(token))
			{
				PrimitiveChoiceSet<Ability> chooser =
						(PrimitiveChoiceSet<Ability>) ChooseLoader.parseToken(
							context, obj, "FEAT", "PC");
				if (chooser == null)
				{
					Logging.errorPrint("Internal Error: REMOVE:"
						+ getTokenName() + " failed to build Chooser");
					return false;
				}
				pcsList.add(chooser);
			}
			else if (token.regionMatches(true, 0, "CLASS.", 0, 6))
			{
				// FIXME Need to parse CLASS.*
				// Hack (to allow compilation)
				CDOMReference<Ability> ref;
				ref = null;
				refs.add(ref);
			}
			else
			{
				foundOther = true;
				CDOMReference<Ability> ref =
						TokenUtilities.getTypeOrPrimitive(context,
							ABILITY_CLASS, AbilityCategory.FEAT, token);
				if (ref == null)
				{
					return false;
				}
				refs.add(ref);
			}
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		if (!refs.isEmpty())
		{
			pcsList.add(new ReferenceChoiceSet<Ability>(refs));
		}
		PrimitiveChoiceSet<Ability> pcs;
		if (pcsList.size() == 1)
		{
			pcs = pcsList.get(0);
		}
		else
		{
			pcs = new CompoundOrChoiceSet<Ability>(pcsList);
		}
		ChoiceSet<Ability> cs = new ChoiceSet<Ability>("REMOVE", pcs);
		PCGraphGrantsEdge edge = context.graph.grant(getTokenName(), obj, cs);
		edge.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(count));
		edge.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE));
		RemoveFactory<Ability> rf = new RemoveFactory<Ability>(edge);
		context.graph.grant(getTokenName(), obj, rf);
		/*
		 * FUTURE Technically, this Category item should not be in the
		 * RemoveFactory, as it really belogs as something that can be extracted
		 * from the ChoiceSet...
		 */
		rf
			.setAssociation(AssociationKey.ABILITY_CATEGORY,
				AbilityCategory.FEAT);
		rf.setAssociation(AssociationKey.ABILITY_NATURE, AbilityNature.NORMAL);
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		GraphChanges<ChoiceSet> choiceChanges =
				context.graph.getChangesFromToken(getTokenName(), obj,
					ChoiceSet.class);
		if (choiceChanges == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = choiceChanges.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		GraphChanges<RemoveFactory> grantChanges =
				context.graph.getChangesFromToken(getTokenName(), obj,
					RemoveFactory.class);
		Collection<LSTWriteable> grantAdded = grantChanges.getAdded();
		if (grantAdded == null || grantAdded.isEmpty())
		{
			// Zero indicates no Token present
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (LSTWriteable lstw : added)
		{
			ChoiceSet<?> cs = (ChoiceSet<?>) lstw;
			if (ABILITY_CLASS.equals(cs.getChoiceClass()))
			{
				AbilityNature nat = null;
				AbilityCategory cat = null;
				for (LSTWriteable rw : grantAdded)
				{
					RemoveFactory<?> rf = (RemoveFactory<?>) rw;
					if (rf.usesChoiceSet(cs))
					{
						cat =
								rf
									.getAssociation(AssociationKey.ABILITY_CATEGORY);
						nat = rf.getAssociation(AssociationKey.ABILITY_NATURE);
						break;
					}
				}
				if (nat == null)
				{
					context
						.addWriteMessage("Unable to find Nature for RemoverFactory");
					return null;
				}
				if (cat == null)
				{
					context
						.addWriteMessage("Unable to find Category for RemoverFactory");
					return null;
				}
				if (!AbilityCategory.FEAT.equals(cat)
					|| !AbilityNature.NORMAL.equals(nat))
				{
					// will be done with VFEAT or ABILITY
					continue;
				}
				AssociatedPrereqObject assoc =
						choiceChanges.getAddedAssociation(lstw);
				Formula f = assoc.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					context.addWriteMessage("Unable to find Choice Count");
					return null;
				}
				String fString = f.toString();
				StringBuilder sb = new StringBuilder();
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
