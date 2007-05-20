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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMCompoundReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.Restriction;
import pcgen.cdom.base.Slot;
import pcgen.cdom.restriction.GroupRestriction;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.SpecialAbility;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.util.Logging;

public class SAToken implements AddLstToken
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
			Logging.errorPrint("Lack of a SUBTOKEN for ADD:SA "
				+ "is prohibited in new syntax.");
			Logging.errorPrint("Please use ADD:SA|name|[count|]X,X");
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

	public String getTokenName()
	{
		return "SA";
	}

	public boolean parse(LoadContext context, PObject obj, String value)
		throws PersistenceLayerException
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

		if (items.length() == 0)
		{
			Logging.errorPrint("Invalid: Empty SAs in ADD:" + getTokenName()
				+ ": " + value);
			return false;
		}

		if (items.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName() + " List may not start with , : "
				+ value);
			return false;
		}
		if (items.charAt(items.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName() + " List may not end with , : "
				+ value);
			return false;
		}
		if (items.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " SA List uses double separator ,, : " + value);
			return false;
		}

		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		Slot<SpecialAbility> slot =
				context.graph.addSlot(getTokenName(), obj, SPECABILITY_CLASS,
					FormulaFactory.getFormulaFor(count));
		slot.setName(name);
		CDOMCompoundReference<SpecialAbility> cr =
				new CDOMCompoundReference<SpecialAbility>(SPECABILITY_CLASS,
					getTokenName() + " items");
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			context.ref.constructIfNecessary(SPECABILITY_CLASS, token);
			cr.addReference(context.ref.getCDOMReference(SPECABILITY_CLASS,
				token));
		}

		slot.addSinkRestriction(new GroupRestriction<SpecialAbility>(
			SPECABILITY_CLASS, cr));
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		GraphChanges<Slot> changes =
				context.graph.getChangesFromToken(getTokenName(), obj,
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
		Set<String> set = new TreeSet<String>();
		for (LSTWriteable lw : added)
		{
			Slot<SpecialAbility> slot = (Slot<SpecialAbility>) lw;
			if (!slot.getSlotClass().equals(SPECABILITY_CLASS))
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
			StringBuilder sb = new StringBuilder();
			sb.append(slot.getName()).append('|');
			if (!"1".equals(slotCount))
			{
				sb.append(slotCount).append('|');
			}
			sb.append(res.toLSTform());
			set.add(sb.toString());
		}
		return set.toArray(new String[set.size()]);
	}
}
