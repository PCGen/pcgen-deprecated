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

import java.util.Collection;

import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.Slot;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.restriction.GroupRestriction;
import pcgen.core.Ability;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GraphChanges;
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

			buf.append("PREMULT:1,[PREHD:1],[PRELEVEL:1]");

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

		Slot<Ability> slot =
				context.graph.addSlot(getTokenName(), race, ABILITY_CLASS,
					FormulaFactory.getFormulaFor(featCount));
		/*
		 * This prereq exists solely to prevent the ability to select Feats
		 * before first level. It simply aligns this to the rules that provide
		 * for the extra feats for a starting character (which is first level)
		 * This was established through questioning of the more senior code
		 * monkeys on one of the lists in early 2007 - Tom Parker Mar/28/2007
		 */
		Prerequisite prereq =
				getPrerequisite("PREMULT:1,[PRELEVEL:1],[PREHD:1]");
		slot.addPreReq(prereq);
		CDOMGroupRef<Ability> ref =
				context.ref.getCDOMAllReference(ABILITY_CLASS,
					AbilityCategory.FEAT);

		slot.addSinkRestriction(new GroupRestriction<Ability>(ABILITY_CLASS,
			ref));
		slot.setAssociation(AssociationKey.ABILITY_CATEGORY,
			AbilityCategory.FEAT);
		slot
			.setAssociation(AssociationKey.ABILITY_NATURE, AbilityNature.NORMAL);
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		GraphChanges<Slot> changes =
				context.graph.getChangesFromToken(getTokenName(), race,
					Slot.class);
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
		if (added.size() > 1)
		{
			context.addWriteMessage("Invalid Slot Count " + added.size()
				+ " associated with " + getTokenName()
				+ ": Only one Slot allowed.");
			return null;
		}
		Slot<Ability> slot = (Slot<Ability>) added.iterator().next();
		if (!slot.getSlotClass().equals(ABILITY_CLASS))
		{
			context.addWriteMessage("Invalid Slot Type associated with "
				+ getTokenName() + ": Type cannot be "
				+ slot.getSlotClass().getSimpleName());
			return null;
		}
		if (!AbilityCategory.FEAT.equals(slot
			.getAssociation(AssociationKey.ABILITY_CATEGORY)))
		{
			context.addWriteMessage("Invalid Ability Category associated with "
				+ getTokenName() + ": Category cannot be "
				+ slot.getAssociation(AssociationKey.ABILITY_CATEGORY));
			return null;
		}
		if (!AbilityNature.NORMAL.equals(slot
			.getAssociation(AssociationKey.ABILITY_NATURE)))
		{
			context.addWriteMessage("Invalid Ability Nature associated with "
				+ getTokenName() + ": Category cannot be "
				+ slot.getAssociation(AssociationKey.ABILITY_NATURE));
			return null;
		}
		return new String[]{slot.getSlotCount()};
	}
}
