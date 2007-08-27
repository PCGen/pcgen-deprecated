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

import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.GrantActor;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.Ability;
import pcgen.core.PCTemplate;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSFEATS Token
 */
public class BonusfeatsToken implements PCTemplateLstToken
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	public String getTokenName()
	{
		return "BONUSFEATS";
	}

	// number of additional feats to spend
	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			int featCount = Integer.parseInt(value);
			if (featCount <= 0)
			{
				Logging.errorPrint("Invalid integer in " + getTokenName()
					+ ": must be greater than zero");
				return false;
			}
			template.setBonusInitialFeats(featCount);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": must be an integer (greater than zero)");
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
		throws PersistenceLayerException
	{
		int featCount;
		try
		{
			featCount = Integer.parseInt(value);
			if (featCount <= 0)
			{
				Logging.errorPrint("Number in " + getTokenName()
					+ " must be greater than zero: " + value);
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
				+ value);
			return false;
		}

		ChooseActionContainer container =
				new ChooseActionContainer(getTokenName());
		container.addActor(new GrantActor<PCTemplate>());
		AssociatedPrereqObject edge =
				context.getGraphContext().grant(getTokenName(), template,
					container);
		edge.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(featCount));
		edge.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(featCount));
		edge.setAssociation(AssociationKey.ABILITY_CATEGORY,
			AbilityCategory.FEAT);
		edge
			.setAssociation(AssociationKey.ABILITY_NATURE, AbilityNature.NORMAL);
		CDOMGroupRef<Ability> ref =
				context.ref.getCDOMAllReference(ABILITY_CLASS,
					AbilityCategory.FEAT);
		ReferenceChoiceSet<Ability> rcs =
				new ReferenceChoiceSet<Ability>(Collections.singletonList(ref));
		ChoiceSet<Ability> cs = new ChoiceSet<Ability>(getTokenName(), rcs);
		edge.setAssociation(AssociationKey.CHOICE, cs);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate template)
	{
		AssociatedChanges<ChooseActionContainer> grantChanges =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					template, ChooseActionContainer.class);
		if (grantChanges == null)
		{
			return null;
		}
		MapToList<LSTWriteable, AssociatedPrereqObject> mtl =
				grantChanges.getAddedAssociations();
		if (mtl == null || mtl.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		String returnString = null;
		for (LSTWriteable lstw : mtl.getKeySet())
		{
			ChooseActionContainer container = (ChooseActionContainer) lstw;
			if (getTokenName().equals(container.getName()))
			{
				if (returnString != null)
				{
					context.addWriteMessage("Found two CHOOSE containers for: "
						+ container.getName());
					continue;
				}
			}
			else
			{
				context.addWriteMessage("Unexpected CHOOSE container found: "
					+ container.getName());
				continue;
			}
			List<AssociatedPrereqObject> assocList = mtl.getListFor(lstw);
			if (assocList.size() != 1)
			{
				context
					.addWriteMessage("Only one Association to a CHOOSE can be made per object");
				return null;
			}
			AssociatedPrereqObject assoc = assocList.get(0);
			ChoiceSet<?> cs = assoc.getAssociation(AssociationKey.CHOICE);
			if (ABILITY_CLASS.equals(cs.getChoiceClass()))
			{
				AbilityNature nat =
						assoc.getAssociation(AssociationKey.ABILITY_NATURE);
				if (nat == null)
				{
					context
						.addWriteMessage("Unable to find Nature for GrantFactory");
					return null;
				}
				AbilityCategory cat =
						assoc.getAssociation(AssociationKey.ABILITY_CATEGORY);
				if (cat == null)
				{
					context
						.addWriteMessage("Unable to find Category for GrantFactory");
					return null;
				}
				if (!AbilityCategory.FEAT.equals(cat)
					|| !AbilityNature.NORMAL.equals(nat))
				{
					// can't handle those here!
					continue;
				}
				Formula f = assoc.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getTokenName()
						+ " Count");
					return null;
				}
				returnString = f.toString();

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return new String[]{returnString};
	}
}
