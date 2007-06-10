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
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.choice.AnyChooser;
import pcgen.cdom.choice.CompoundOrChooser;
import pcgen.cdom.choice.RemovingChooser;
import pcgen.cdom.enumeration.EqWield;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.filter.TypeFilter;
import pcgen.cdom.helper.ChoiceFilter;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class WeaponProfToken implements ChooseLstToken
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
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
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

	public String getTokenName()
	{
		return "WEAPONPROF";
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
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
			return null;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
			return null;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
			return null;
		}
		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			int count;
			try
			{
				count = Integer.parseInt(value);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " first argument must be an Integer : " + value);
				return null;
			}
			AnyChooser<WeaponProf> base =
					new AnyChooser<WeaponProf>(WeaponProf.class);
			base.setCount(FormulaFactory.getFormulaFor(count));
			return base;
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

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		List<CDOMReference<WeaponProf>> refList =
				new ArrayList<CDOMReference<WeaponProf>>();
		ArrayList<ChoiceFilter<? super WeaponProf>> filterList =
				new ArrayList<ChoiceFilter<? super WeaponProf>>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if ("DEITYWEAPON".equalsIgnoreCase(tokString))
			{

			}
			else if (tokString.startsWith("FEAT="))
			{
				// TODO need CASE insensitive :P

			}
			else if (tokString.startsWith("WEILD."))
			{
				EqWield w = EqWield.valueOf(tokString.substring(6));
				if (w == null)
				{
					Logging.errorPrint("Unknown Wield Type: " + tokString);
					Logging.errorPrint("  entire token was: " + value);
					return null;
				}
				// TODO need CASE insensitive :P

			}
			else if (tokString.startsWith("!TYPE=")
				|| tokString.startsWith("!TYPE."))
			{

				StringTokenizer typeTok =
						new StringTokenizer(tokString.substring(6), ".");
				List<Type> list = new ArrayList<Type>();
				while (typeTok.hasMoreTokens())
				{
					Type requiredType = Type.getConstant(typeTok.nextToken());
					list.add(requiredType);
				}
				TypeFilter tf = new TypeFilter(list);
				filterList.add(tf);
			}
			else
			{
				// This captures positive TYPE references
				CDOMReference<WeaponProf> ref =
						TokenUtilities.getTypeOrPrimitive(context,
							WeaponProf.class, tok.nextToken());
				refList.add(ref);
			}
		}
		// TODO add refList
		// TODO check if OR is really necessary??
		CompoundOrChooser<WeaponProf> chooser =
			new CompoundOrChooser<WeaponProf>();
		ChoiceSet<WeaponProf> retChooser = chooser;
		if (filterList.isEmpty())
		{
			RemovingChooser<WeaponProf> rc =
					new RemovingChooser<WeaponProf>(chooser);
			for (ChoiceFilter<? super WeaponProf> f : filterList)
			{
				rc.addRemovingChoiceFilter(f);
			}
			retChooser = rc;
		}
		// TODO set Count
		return retChooser;
	}
}
