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

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMCompoundReference;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.Restriction;
import pcgen.cdom.choice.GrantedChooser;
import pcgen.cdom.choice.RemovingChooser;
import pcgen.cdom.content.Remover;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.filter.CategoryFilter;
import pcgen.cdom.filter.NegatingFilter;
import pcgen.cdom.restriction.GroupRestriction;
import pcgen.core.Ability;
import pcgen.core.PObject;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.RemoveLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class FeatToken implements RemoveLstToken
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

		if (items.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , see: " + value);
			return false;
		}
		if (items.charAt(items.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with , see: " + value);
			return false;
		}
		if (items.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);

		Remover<Ability> rem =
				new Remover<Ability>(ABILITY_CLASS, FormulaFactory
					.getFormulaFor(count));
		CDOMCompoundReference<Ability> cr =
				new CDOMCompoundReference<Ability>(ABILITY_CLASS,
					getTokenName() + " items");
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<Ability> ref;
			if (Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				ref =
						context.ref.getCDOMAllReference(ABILITY_CLASS,
							AbilityCategory.FEAT);
			}
			else if ("CHOICE".equalsIgnoreCase(token))
			{
				GrantedChooser<Ability> gc =
						GrantedChooser.getGrantedChooser(Ability.class);
				RemovingChooser<Ability> rc = new RemovingChooser<Ability>(gc);
				CategoryFilter<Ability> cf =
						new CategoryFilter<Ability>(AbilityCategory.FEAT);
				rc
					.addRemovingChoiceFilter(NegatingFilter
						.getNegatingFilter(cf));
				rc.setCount(FormulaFactory.getFormulaFor(count));

				// FIXME Need to parse CHOICE (granted Chooser?)
				//Hack (to allow compilation)
				ref = null;
			}
			else if (token.regionMatches(true, 0, "CLASS.", 0, 6))
			{
				// FIXME Need to parse CLASS.*
				//Hack (to allow compilation)
				ref = null;
			}
			else
			{
				foundOther = true;
				ref =
						TokenUtilities.getTypeOrPrimitive(context,
							ABILITY_CLASS, AbilityCategory.FEAT, token);
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
		rem
			.addSinkRestriction(new GroupRestriction<Ability>(ABILITY_CLASS, cr));
		context.graph.grant(getTokenName(), obj, rem);
		rem.setAssociation(AssociationKey.ABILITY_CATEGORY,
			AbilityCategory.FEAT);
		rem.setAssociation(AssociationKey.ABILITY_NATURE, AbilityNature.NORMAL);
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		GraphChanges<Remover> changes =
				context.graph.getChangesFromToken(getTokenName(), obj,
					Remover.class);
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
			context.addWriteMessage("Invalid Count " + added.size()
				+ " associated with " + getTokenName()
				+ ": Only one Remove allowed.");
			return null;
		}
		Remover<Ability> rem = (Remover<Ability>) added.iterator().next();
		if (!rem.getRemovedClass().equals(ABILITY_CLASS))
		{
			context.addWriteMessage("Invalid Slot Type associated with "
				+ getTokenName() + ": Type cannot be "
				+ rem.getRemovedClass().getSimpleName());
			return null;
		}
		String slotCount = rem.getLSTformat();
		String result;
		List<Restriction<?>> restr = rem.getSinkRestrictions();
		if (restr.size() != 1)
		{
			context.addWriteMessage("Remover for " + getTokenName()
				+ " must have only one restriction");
			return null;
		}
		Restriction<?> res = restr.get(0);
		if ("1".equals(slotCount))
		{
			result = res.toLSTform();
		}
		else
		{
			result = slotCount + "|" + res.toLSTform();
		}
		return new String[]{result};
	}
}
