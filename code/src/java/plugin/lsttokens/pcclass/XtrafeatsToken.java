/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

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
import pcgen.core.PCClass;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with XTRAFEATS Token
 */
public class XtrafeatsToken implements PCClassLstToken, PCClassClassLstToken
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	/**
	 * Get Token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "XTRAFEATS";
	}

	/**
	 * Parse the XTRAFEATS token
	 * 
	 * @param pcclass
	 * @param value
	 * @param level
	 * @return true if successful else false
	 */
	public boolean parse(PCClass pcclass, String value, int level)
	{
		try
		{
			pcclass.setInitialFeats(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
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

		Slot<Ability> slot =
				context.graph.addSlot(getTokenName(), pcc,
					ABILITY_CLASS, FormulaFactory.getFormulaFor(featCount));
		/*
		 * Unlike Race's STARTFEATS, no prereq is required here since this in a
		 * PCClass, it guarantees the Character is at least level 1. - Tom
		 * Parker Apr 7, 2007
		 */
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

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		GraphChanges<Slot> changes =
				context.graph.getChangesFromToken(getTokenName(), pcc,
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
			context.addWriteMessage("Error in " + pcc.getKeyName()
				+ ": Only one " + getTokenName()
				+ " Slot is allowed per PCClass");
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
