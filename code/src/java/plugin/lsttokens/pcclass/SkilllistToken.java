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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMCompoundReference;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.Restriction;
import pcgen.cdom.base.Slot;
import pcgen.cdom.restriction.GroupRestriction;
import pcgen.core.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with SKILLLIST Token
 */
public class SkilllistToken extends AbstractToken implements PCClassLstToken,
		PCClassClassLstToken
{

	private static final Class<ClassSkillList> SKILLLIST_CLASS =
			ClassSkillList.class;

	@Override
	public String getTokenName()
	{
		return "SKILLLIST";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|");
		int skillCount = 0;

		if (value.indexOf('|') >= 0)
		{
			try
			{
				skillCount = Integer.parseInt(aTok.nextToken());
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Import error: Expected first value of "
					+ "SKILLLIST token with a | to be a number");
				return false;
			}
		}

		final List<String> skillChoices = new ArrayList<String>();

		while (aTok.hasMoreTokens())
		{
			skillChoices.add(aTok.nextToken());
		}

		// Protection against a "" value parameter
		if (skillChoices.size() > 0)
		{
			pcclass.setClassSkillChoices(skillCount, skillChoices);
		}
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		if (value.indexOf('|') == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " may not have only one argument");
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		int count;
		try
		{
			count = Integer.parseInt(tok.nextToken());
			if (count <= 0)
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

		Slot<ClassSkillList> slot =
				context.graph.addSlot(getTokenName(), pcc, SKILLLIST_CLASS,
					FormulaFactory.getFormulaFor(count));

		CDOMCompoundReference<ClassSkillList> cr =
				new CDOMCompoundReference<ClassSkillList>(SKILLLIST_CLASS,
					getTokenName() + " items");

		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<ClassSkillList> ref;
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(SKILLLIST_CLASS);
			}
			else
			{
				foundOther = true;
				ref =
						TokenUtilities.getTypeOrPrimitive(context,
							SKILLLIST_CLASS, token);
			}
			if (ref == null)
			{
				return false;
			}
			cr.addReference(ref);
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		slot.addSinkRestriction(new GroupRestriction<ClassSkillList>(
			SKILLLIST_CLASS, cr));

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
		Slot<?> slot = (Slot<?>) added.iterator().next();
		if (!slot.getSlotClass().equals(SKILLLIST_CLASS))
		{
			context.addWriteMessage("Invalid Slot Type associated with "
				+ getTokenName() + ": Type cannot be "
				+ slot.getSlotClass().getSimpleName());
			return null;
		}
		String slotCount = slot.getSlotCount();
		List<Restriction<?>> restr = slot.getSinkRestrictions();
		if (restr.size() != 1)
		{
			context.addWriteMessage("Slot for " + getTokenName()
				+ " must have only one restriction");
			return null;
		}
		Restriction<?> res = restr.get(0);
		if (!res.getRestrictedType().equals(SKILLLIST_CLASS))
		{
			context.addWriteMessage("Invalid Restriction Type associated with "
				+ getTokenName() + ": Type cannot be "
				+ slot.getSlotClass().getSimpleName());
			return null;
		}
		return new String[]{(slotCount + "|" + res.toLSTform())};
	}
}
