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
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.choice.AnyChooser;
import pcgen.cdom.choice.CompoundAndChooser;
import pcgen.cdom.choice.GrantedChooser;
import pcgen.cdom.choice.ReferenceChooser;
import pcgen.cdom.choice.RemovingChooser;
import pcgen.cdom.filter.PCChoiceFilter;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.ArmorProf;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.ShieldProf;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class ProficiencyToken extends AbstractToken implements ChooseLstToken
{

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
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() < 3)
		{
			Logging.errorPrint("COUNT:" + getTokenName()
				+ " requires at least three arguments: " + value);
			return false;
		}
		String first = tok.nextToken();
		if (!first.equals("ARMOR") && !first.equals("SHIELD")
			&& !first.equals("WEAPON"))
		{
			Logging.errorPrint("COUNT:" + getTokenName()
				+ " first argument was not ARMOR, SHIELD, or WEAPON");
			return false;
		}
		String second = tok.nextToken();
		if (!second.equals("PC") && !second.equals("ALL")
			&& !second.equals("UNIQUE"))
		{
			Logging.errorPrint("COUNT:" + getTokenName()
				+ " second argument was not PC, ALL, or UNIQUE");
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
		return "PROFICIENCY";
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
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() < 3)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " requires at least three arguments: " + value);
			return null;
		}
		String first = tok.nextToken();
		Class<? extends PObject> cl;
		if (first.equals("ARMOR"))
		{
			cl = ArmorProf.class;
		}
		else if (first.equals("SHIELD"))
		{
			cl = ShieldProf.class;
		}
		else if (first.equals("WEAPON"))
		{
			cl = WeaponProf.class;
		}
		else
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " first argument was not ARMOR, SHIELD, or WEAPON");
			return null;
		}
		return continueProcessing(context, tok, cl);
	}

	private <T extends PObject> ChoiceSet<T> continueProcessing(
		LoadContext context, StringTokenizer tok, Class<T> cl)
	{
		String tokString = tok.nextToken();
		String second = tokString;
		CompoundAndChooser<T> chooser = new CompoundAndChooser<T>();
		if (second.equals("PC"))
		{
			chooser.addChoiceSet(new GrantedChooser<T>(cl), true);
		}
		else if (second.equals("ALL"))
		{
			chooser.addChoiceSet(new AnyChooser<T>(cl), true);
			// TODO IF second is all, is a third required??
		}
		else if (second.equals("UNIQUE"))
		{
			AnyChooser<T> base = new AnyChooser<T>(cl);
			RemovingChooser<T> rem = new RemovingChooser<T>(base, false);
			rem.addRemovingChoiceFilter(new PCChoiceFilter<T>(cl), true);
			chooser.addChoiceSet(rem, true);
		}
		else
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " second argument was not PC, ALL, or UNIQUE");
			return null;
		}
		List<CDOMReference<T>> refList = new ArrayList<CDOMReference<T>>();
		while (tok.hasMoreTokens())
		{
			CDOMReference<T> ref =
					TokenUtilities.getTypeOrPrimitive(context, cl, tokString);
			if (ref == null)
			{
				Logging.errorPrint("Invalid Reference: " + tokString
					+ " in CHOOSE:" + getTokenName());
				return null;
			}
			refList.add(ref);
		}
		chooser.addChoiceSet(new ReferenceChooser<T>(refList), true);
		return chooser;
	}

	public String unparse(LoadContext context, ChoiceSet<?> chooser)
	{
		return chooser.getLSTformat();
	}
}
