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
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.CompoundOrChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.cdom.helper.RemoveActor;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.RemoveLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class FeatToken extends AbstractToken implements RemoveLstToken,
		CDOMSecondaryToken<CDOMObject>
{
	private static final Class<CDOMAbility> ABILITY_CLASS = CDOMAbility.class;

	public String getParentToken()
	{
		return "REMOVE";
	}

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

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

	public boolean parse(LoadContext context, CDOMObject obj, String value)
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
					Logging.errorPrint("Count in " + getFullName()
							+ " must be > 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid Count in " + getFullName()
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

		List<CDOMReference<CDOMAbility>> refs = new ArrayList<CDOMReference<CDOMAbility>>();
		List<PrimitiveChoiceSet<CDOMAbility>> pcsList = new ArrayList<PrimitiveChoiceSet<CDOMAbility>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				CDOMReference<CDOMAbility> ref = context.ref
						.getCDOMAllReference(ABILITY_CLASS,
								CDOMAbilityCategory.FEAT);
				refs.add(ref);
			}
			else if ("CHOICE".equalsIgnoreCase(token))
			{
				// TODO Need to process Grouping: CATEGORY, NATURE, etc.
				PrimitiveChoiceSet<CDOMAbility> chooser = context.getChoiceSet(
						CDOMAbility.class, "PC");
				if (chooser == null)
				{
					Logging.errorPrint("Internal Error: "
							+ getFullName() + " failed to build Chooser");
					return false;
				}
				pcsList.add(chooser);
			}
			else if (token.regionMatches(true, 0, "CLASS.", 0, 6))
			{
				//String className = token.substring(6);
				// FIXME Need to parse CLASS.*
				// Hack (to allow compilation)
				CDOMReference<CDOMAbility> ref;
				ref = null;
				refs.add(ref);
			}
			else
			{
				foundOther = true;
				CDOMReference<CDOMAbility> ref = TokenUtilities
						.getTypeOrPrimitive(context, ABILITY_CLASS,
								CDOMAbilityCategory.FEAT, token);
				if (ref == null)
				{
					return false;
				}
				refs.add(ref);
			}
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		if (!refs.isEmpty())
		{
			pcsList.add(new ReferenceChoiceSet<CDOMAbility>(refs));
		}
		PrimitiveChoiceSet<CDOMAbility> pcs;
		if (pcsList.size() == 1)
		{
			pcs = pcsList.get(0);
		}
		else
		{
			pcs = new CompoundOrChoiceSet<CDOMAbility>(pcsList);
		}

		ChooseActionContainer container = new ChooseActionContainer("REMOVE");
		container.addActor(new RemoveActor());
		context.getObjectContext().give(getFullName(), obj, container);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
				.getFormulaFor(count));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE));
		container.setAssociation(AssociationKey.ABILITY_CATEGORY,
				CDOMAbilityCategory.FEAT);
		container.setAssociation(AssociationKey.ABILITY_NATURE,
				AbilityNature.NORMAL);
		ChoiceSet<CDOMAbility> cs = new ChoiceSet<CDOMAbility>("REMOVE", pcs);
		container.setChoiceSet(cs);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<ChooseActionContainer> grantChanges = context
				.getObjectContext().getGivenChanges(getFullName(), obj,
						ChooseActionContainer.class);
		Collection<ChooseActionContainer> addedItems = grantChanges.getAdded();
		if (addedItems == null || addedItems.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> removeStrings = new ArrayList<String>();
		for (LSTWriteable lstw : addedItems)
		{
			ChooseActionContainer container = (ChooseActionContainer) lstw;
			if (!"REMOVE".equals(container.getName()))
			{
				context.addWriteMessage("Unexpected CHOOSE container found: "
						+ container.getName());
				continue;
			}
			ChoiceSet<?> cs = container.getChoiceSet();
			if (ABILITY_CLASS.equals(cs.getChoiceClass()))
			{
				AbilityNature nat = container
						.getAssociation(AssociationKey.ABILITY_NATURE);
				if (nat == null)
				{
					context
							.addWriteMessage("Unable to find Nature for GrantFactory");
					return null;
				}
				CDOMAbilityCategory cat = container
						.getAssociation(AssociationKey.ABILITY_CATEGORY);
				if (cat == null)
				{
					context
							.addWriteMessage("Unable to find Category for GrantFactory");
					return null;
				}
				if (!CDOMAbilityCategory.FEAT.equals(cat)
						|| !AbilityNature.NORMAL.equals(nat))
				{
					// can't handle those here!
					continue;
				}
				Formula f = container
						.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName()
							+ " Count");
					return null;
				}
				String fString = f.toString();
				StringBuilder sb = new StringBuilder();
				if (!"1".equals(fString))
				{
					sb.append(fString).append(Constants.PIPE);
				}
				sb.append(cs.getLSTformat());
				removeStrings.add(sb.toString());

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return removeStrings.toArray(new String[removeStrings.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
