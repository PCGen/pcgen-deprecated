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
package plugin.lsttokens.race;

import java.util.Collections;

import pcgen.base.formula.Formula;
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
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

/**
 * Class deals with STARTFEATS Token
 */
public class StartfeatsToken extends AbstractToken implements RaceLstToken
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "STARTFEATS";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append("FEAT|POOL|").append(Integer.parseInt(value));

			final BonusObj bon = Bonus.newBonus(sb.toString());
			final PreParserFactory factory = PreParserFactory.getInstance();
			final StringBuffer buf = new StringBuffer();

			buf.append("PREMULT:1,[PREHD:MIN=1],[PRELEVEL:MIN=1]");

			final Prerequisite prereq = factory.parse(buf.toString());
			bon.addPreReq(prereq);

			race.setBonusInitialFeats(bon);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Caught " + e);
			return false;
		}
	}

	public boolean parse(LoadContext context, Race race, String value)
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
		context.getGraphContext().grant(getTokenName(), race, container);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(featCount));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(featCount));
		container.setAssociation(AssociationKey.ABILITY_CATEGORY,
			AbilityCategory.FEAT);
		container.setAssociation(AssociationKey.ABILITY_NATURE,
			AbilityNature.NORMAL);
		CDOMGroupRef<Ability> ref =
				context.ref.getCDOMAllReference(ABILITY_CLASS,
					AbilityCategory.FEAT);
		ReferenceChoiceSet<Ability> rcs =
				new ReferenceChoiceSet<Ability>(Collections.singletonList(ref));
		ChoiceSet<Ability> cs = new ChoiceSet<Ability>(getTokenName(), rcs);
		container.setChoiceSet(cs);

		/*
		 * This prereq exists solely to prevent the ability to select Feats
		 * before first level. It simply aligns this to the rules that provide
		 * for the extra feats for a starting character (which is first level)
		 * This was established through questioning of the more senior code
		 * monkeys on one of the lists in early 2007 - Tom Parker Mar/28/2007
		 */
		Prerequisite prereq =
				getPrerequisite("PREMULT:1,[PRELEVEL:1],[PREHD:1+]");
		container.addPrerequisite(prereq);
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		AssociatedChanges<ChooseActionContainer> grantChanges =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					race, ChooseActionContainer.class);
		if (grantChanges == null)
		{
			return null;
		}
		if (!grantChanges.hasAddedItems())
		{
			// Zero indicates no Token
			return null;
		}
		String returnString = null;
		for (LSTWriteable lstw : grantChanges.getAdded())
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
			ChoiceSet<?> cs = container.getChoiceSet();
			if (ABILITY_CLASS.equals(cs.getChoiceClass()))
			{
				AbilityNature nat =
						container.getAssociation(AssociationKey.ABILITY_NATURE);
				if (nat == null)
				{
					context
						.addWriteMessage("Unable to find Nature for GrantFactory");
					return null;
				}
				AbilityCategory cat =
						container
							.getAssociation(AssociationKey.ABILITY_CATEGORY);
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
				Formula f =
						container.getAssociation(AssociationKey.CHOICE_COUNT);
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
