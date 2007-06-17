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
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.choice.AnyChooser;
import pcgen.cdom.choice.CompoundAndChooser;
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

public class WeaponProfsToken extends AbstractToken implements ChooseLstToken
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
		return "WEAPONPROFS";
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
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
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
			else if ("LIST".equalsIgnoreCase(tokString))
			{
				choiceList
					.add(new GrantedChooser<WeaponProf>(WeaponProf.class));
			}
			else if (tokString.regionMatches(true, 0, "SIZE.", 0, 5))
			{
				String size = tokString.substring(5, 6);
				CDOMSimpleSingleRef<WeaponProf> ref =
						context.ref.getCDOMReference(WeaponProf.class,
							tokString.substring(6));
				// TODO Should this be a HashMapToList to be built later, or
				// build lots of individual ReferenceChoosers?
			}
			else if (tokString.regionMatches(true, 0, "WSIZE.", 0, 6))
			{
				StringTokenizer typeTok =
						new StringTokenizer(tokString.substring(6), ".");
				String wield = tok.nextToken();
				List<Type> list = new ArrayList<Type>();
				while (typeTok.hasMoreTokens())
				{
					Type requiredType = Type.getConstant(typeTok.nextToken());
					list.add(requiredType);
				}
				AnyChooser<Equipment> ac =
						AnyChooser.getAnyChooser(Equipment.class);
				RemovingChooser<Equipment> rc =
						new RemovingChooser<Equipment>(ac, false);
				ObjectKeyFilter<Equipment> okf =
						new ObjectKeyFilter<Equipment>(Equipment.class);
				okf.setObjectFilter(ObjectKey.WIELD, EqWield.valueOf(wield));
				rc.addRemovingChoiceFilter(NegatingFilter
					.getNegatingFilter(okf), true);
				// rc now captures all Equipment that matches the Wield
				TypeFilter tf = new TypeFilter(list);
				rc.addRemovingChoiceFilter(tf, true);
				// rc now captures all Equipment that matches the Wield and Type
				ObjectKeyTransformer<WeaponProf> okt =
						new ObjectKeyTransformer<WeaponProf>(rc,
							ObjectKey.WEAPON_PROF);
				CompoundAndChooser<WeaponProf> cac =
						new CompoundAndChooser<WeaponProf>();
				cac.addChoiceSet(okt, true);
				cac.addChoiceSet(new GrantedChooser<WeaponProf>(
					WeaponProf.class), false);
				choiceList.add(cac);
			}
			else if (tokString.regionMatches(true, 0, "ADD.", 0, 4))
			{
				CDOMSimpleSingleRef<WeaponProf> ref =
						context.ref.getCDOMReference(WeaponProf.class,
							tokString.substring(4));
				// TODO is this really refList or something added at the end
				// after the NOT stuff is handled...
				refList.add(ref);
			}
			else if (tokString.startsWith("!TYPE=")
				|| tokString.startsWith("!TYPE."))
			{
				// TODO Check for NOT (instead of !)
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
		if (filterList.isEmpty())
		{
			RemovingChooser<WeaponProf> rc =
					new RemovingChooser<WeaponProf>(retChooser, true);
			for (ChoiceFilter<? super WeaponProf> f : filterList)
			{
				rc.addRemovingChoiceFilter(f, true);
			}
			retChooser = rc;
		}
		return retChooser;
	}

	public String unparse(LoadContext context, ChoiceSet<?> chooser)
	{
		return chooser.getLSTformat();
	}
}
