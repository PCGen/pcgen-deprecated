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
package plugin.lsttokens.choose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.choice.AnyChooser;
import pcgen.cdom.choice.CompoundAndChooser;
import pcgen.cdom.choice.ReferenceChooser;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class ArmorProfToken extends AbstractToken implements ChooseLstToken
{

	private static final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return false;
		}
		if (hasIllegalSeparator('|', value))
		{
			return false;
		}

		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " must have two or more | delimited arguments : " + value);
			return false;
		}
		String start = value.substring(0, pipeLoc);
		try
		{
			Integer.parseInt(start);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " first argument must be an Integer : " + value);
			return false;
		}
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		po.setChoiceString(sb.toString());
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "ARMORPROF";
	}

	public ChoiceSet<?> parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
			return null;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value);
			return null;
		}
		if (hasIllegalSeparator('|', value))
		{
			return null;
		}

		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " must have two or more | delimited arguments : " + value);
			return null;
		}
		int count;
		try
		{
			count = Integer.parseInt(value.substring(0, pipeLoc));
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " first argument must be an Integer : " + value);
			return null;
		}
		String rest = value.substring(pipeLoc + 1);
		ChoiceSet<Equipment> cs;
		if (Constants.LST_ANY.equals(rest))
		{
			cs = new AnyChooser<Equipment>(Equipment.class);
		}
		else
		{
			cs = getReferenceChooser(context, rest);
			if (cs == null)
			{
				return null;
			}
		}
		CompoundAndChooser<Equipment> chooser =
				new CompoundAndChooser<Equipment>();
		chooser.addChoiceSet(cs, true);
		CDOMReference<Equipment> armor =
				TokenUtilities.getTypeReference(context, EQUIPMENT_CLASS,
					"Armor");
		chooser.addChoiceSet(new ReferenceChooser<Equipment>(Collections
			.singletonList(armor)), false);
		chooser.setMaxSelections(FormulaFactory.getFormulaFor(count));
		return chooser;
	}

	private ChoiceSet<Equipment> getReferenceChooser(LoadContext context,
		String rest)
	{
		StringTokenizer st = new StringTokenizer(rest, Constants.PIPE);
		List<CDOMReference<Equipment>> eqList =
				new ArrayList<CDOMReference<Equipment>>();
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			if (Constants.LST_ANY.equals(tokString))
			{
				Logging.errorPrint("In CHOOSE:" + getTokenName()
					+ ": Cannot use ANY and another qualifier: " + rest);
				return null;
			}
			else
			{
				CDOMReference<Equipment> ref =
						TokenUtilities.getTypeOrPrimitive(context,
							EQUIPMENT_CLASS, tokString);
				if (ref == null)
				{
					Logging.errorPrint("Invalid Reference: " + tokString
						+ " in CHOOSE:" + getTokenName() + ": " + rest);
					return null;
				}
				eqList.add(ref);
			}
		}
		return new ReferenceChooser<Equipment>(eqList);
	}

	public String unparse(LoadContext context, ChoiceSet<?> chooser)
	{
		return chooser.getMaxSelections().toString() + '|'
			+ chooser.getLSTformat();
	}
}
