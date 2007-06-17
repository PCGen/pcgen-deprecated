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
import pcgen.cdom.choice.GrantedChooser;
import pcgen.cdom.choice.ListKeyTransformer;
import pcgen.cdom.choice.ObjectKeyTransformer;
import pcgen.cdom.choice.ReferenceChooser;
import pcgen.cdom.choice.RemovingChooser;
import pcgen.cdom.enumeration.EqWield;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.filter.NegatingFilter;
import pcgen.cdom.filter.ObjectKeyFilter;
import pcgen.cdom.filter.TypeFilter;
import pcgen.cdom.helper.ChoiceFilter;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class WeaponProfToken extends AbstractToken implements ChooseLstToken
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
		if (hasIllegalSeparator('|', value))
		{
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

		String rest = value.substring(pipeLoc + 1);
		// TODO Support ANY as a looping mechanism for number only
		// TODO Maybe add support for ANY and !TYPE?
		StringTokenizer tok = new StringTokenizer(rest, Constants.PIPE);
		List<ChoiceSet<WeaponProf>> choiceList =
				new ArrayList<ChoiceSet<WeaponProf>>();
		List<CDOMReference<WeaponProf>> refList =
				new ArrayList<CDOMReference<WeaponProf>>();
		ArrayList<ChoiceFilter<? super WeaponProf>> filterList =
				new ArrayList<ChoiceFilter<? super WeaponProf>>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if ("DEITYWEAPON".equalsIgnoreCase(tokString))
			{
				choiceList.add(new ListKeyTransformer<WeaponProf>(
					GrantedChooser.getGrantedChooser(Deity.class),
					ListKey.DEITYWEAPON));
			}
			else if (tokString.regionMatches(true, 0, "FEAT=", 0, 5))
			{
				// TODO need Implementation
			}
			else if (tokString.regionMatches(true, 0, "WIELD.", 0, 6))
			{
				EqWield w = EqWield.valueOf(tokString.substring(6));
				if (w == null)
				{
					Logging.errorPrint("Unknown Wield Type: " + tokString);
					Logging.errorPrint("  entire token was: " + value);
					return null;
				}
				RemovingChooser<Equipment> rc =
						new RemovingChooser<Equipment>(
							new AnyChooser<Equipment>(Equipment.class), false);
				ObjectKeyFilter<Equipment> of =
						ObjectKeyFilter.getObjectFilter(Equipment.class);
				of.setObjectFilter(ObjectKey.WIELD, w);
				rc.addRemovingChoiceFilter(
					NegatingFilter.getNegatingFilter(of), true);
				ObjectKeyTransformer<WeaponProf> okt =
						new ObjectKeyTransformer<WeaponProf>(rc,
							ObjectKey.WEAPON_PROF);
				choiceList.add(okt);
			}
			else if (tokString.startsWith("!TYPE=")
				|| tokString.startsWith("!TYPE."))
			{
				String typeString = tokString.substring(6);
				if (typeString.length() == 0)
				{
					Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " Type arguments may not be empty: " + value);
					return null;
				}
				if (hasIllegalSeparator('.', typeString))
				{
					Logging.errorPrint("  Entire CHOOSE Token was: " + value);
					return null;
				}
				StringTokenizer typeTok = new StringTokenizer(typeString, ".");
				List<Type> list = new ArrayList<Type>();
				while (typeTok.hasMoreTokens())
				{
					Type requiredType = Type.getConstant(typeTok.nextToken());
					list.add(requiredType);
				}
				TypeFilter tf = new TypeFilter(list);
				filterList.add(NegatingFilter.getNegatingFilter(tf));
			}
			else
			{
				// This captures positive TYPE references
				CDOMReference<WeaponProf> ref =
						TokenUtilities.getTypeOrPrimitive(context,
							WeaponProf.class, tokString);
				if (ref == null)
				{
					Logging.errorPrint("Invalid Reference: " + tokString
						+ " in CHOOSE:" + getTokenName() + ": " + value);
					return null;
				}
				refList.add(ref);
			}
		}
		if (!refList.isEmpty())
		{
			choiceList.add(new ReferenceChooser<WeaponProf>(refList));
		}
		ChoiceSet<WeaponProf> retChooser;
		int size = choiceList.size();
		if (size == 0)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
				+ " must have at least one item that adds to the list");
			return null;
		}
		else if (size == 1)
		{
			retChooser = choiceList.get(0);
		}
		else
		{
			CompoundOrChooser<WeaponProf> chooser =
					new CompoundOrChooser<WeaponProf>();
			chooser.addAllChoiceSets(choiceList);
			retChooser = chooser;
		}
		if (!filterList.isEmpty())
		{
			RemovingChooser<WeaponProf> rc =
					new RemovingChooser<WeaponProf>(retChooser, true);
			for (ChoiceFilter<? super WeaponProf> f : filterList)
			{
				rc.addRemovingChoiceFilter(f, true);
			}
			retChooser = rc;
		}
		retChooser.setCount(FormulaFactory.getFormulaFor(count));
		return retChooser;
	}

	public String unparse(LoadContext context, ChoiceSet<?> chooser)
	{
		return chooser.getCount().toString() + "|" + chooser.getLSTformat();
	}
}
