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
import pcgen.cdom.base.Constants;
import pcgen.cdom.choiceset.CompoundOrChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.ChoiceSetCompatibilityToken;
import pcgen.util.Logging;

public class WeaponProfsToken extends AbstractToken implements ChooseLstToken,
		ChoiceSetCompatibilityToken<CDOMObject>
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " requires additional arguments");
			return false;
		}
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
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			int equalsLoc = tokString.indexOf("=");
			if (equalsLoc == tokString.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments must have value after = : " + tokString);
				Logging.errorPrint("  entire token was: " + value);
				return false;
			}
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

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public PrimitiveChoiceSet<?> parse(LoadContext context, CDOMObject cdo,
			String value) throws PersistenceLayerException
	{
		List<PrimitiveChoiceSet<CDOMWeaponProf>> pcsList = new ArrayList<PrimitiveChoiceSet<CDOMWeaponProf>>();
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			if (Constants.LST_LIST.equals(tokString))
			{
				pcsList.add(context.getChoiceSet(CDOMWeaponProf.class, "PC"));
			}
			else if ("DEITYWEAPON".equals(tokString))
			{
				pcsList.add(context.getChoiceSet(CDOMWeaponProf.class,
						"DEITYWEAPON"));
			}
			else if (tokString.startsWith("ADD."))
			{
				pcsList.add(context.getChoiceSet(CDOMWeaponProf.class,
						tokString.substring(4)));
			}
			else
			{
				pcsList.add(context.getChoiceSet(CDOMWeaponProf.class, "PC["
						+ tokString + "]"));
			}
		}
		// while (choicesIt.hasNext())
		// {
		// final String aString = choicesIt.next();
		// if ("SIZE.".regionMatches(true, 0, aString, 0, 5))
		// {
		// final String profKey = aString.substring(7);
		// if ((aPc.sizeInt() >= Globals.sizeInt(aString.substring(5, 6)))
		// && aPc.hasWeaponProfKeyed(profKey))
		// {
		// final WeaponProf wp = Globals.getWeaponProfKeyed(profKey);
		// if (!availableList.contains(wp))
		// {
		// availableList.add(wp);
		// }
		// }
		// }
		// else if ("WSIZE.".regionMatches(true, 0, aString, 0, 6))
		// {
		// final StringTokenizer bTok = new StringTokenizer(aString, ".");
		// bTok.nextToken(); // should be WSize
		//
		// final String sString = bTok.nextToken(); // should be Light,
		// // 1 handed, 2
		// // handed choices
		// // above
		// final List<String> typeList = new ArrayList<String>();
		//
		// while (bTok.hasMoreTokens()) // any additional constraints
		// {
		// final String dString = bTok.nextToken().toUpperCase();
		// typeList.add(dString);
		// }
		//
		// for (final WeaponProf wp : aPc.getWeaponProfs())
		// {
		// if (wp == null)
		// {
		// continue;
		// }
		//
		// //
		// // get an Equipment object based on the named WeaponProf
		// //
		// final String profKey = wp.getKeyName();
		// Equipment eq = EquipmentList.getEquipmentNamed(profKey);
		//
		// if (eq == null)
		// {
		// //
		// // Sword (Bastard/Exotic), Sword (Bastard/Martial),
		// // Katana (Martial), Katana(Exotic)
		// //
		// int len = 0;
		//
		// if (profKey.endsWith("Exotic)"))
		// {
		// len = 7;
		// }
		//
		// if ((len == 0) && profKey.endsWith("Martial)"))
		// {
		// len = 8;
		// }
		//
		// if (len != 0)
		// {
		// if (profKey.charAt(profKey.length() - len - 1) == '/')
		// {
		// ++len;
		// }
		//
		// String tempString =
		// profKey
		// .substring(0, profKey.length() - len)
		// + ")";
		//
		// if (tempString.endsWith("()"))
		// {
		// tempString =
		// tempString.substring(0,
		// tempString.length() - 3).trim();
		// }
		//
		// eq = EquipmentList.getEquipmentNamed(tempString);
		// }
		// else
		// {
		// //
		// // Couldn't find equipment with matching name, look
		// // for 1st weapon that uses it
		// //
		// for (Iterator<Map.Entry<String, Equipment>> eqIter =
		// EquipmentList.getEquipmentListIterator(); eqIter
		// .hasNext();)
		// {
		// final Map.Entry<String, Equipment> entry =
		// eqIter.next();
		// final Equipment tempEq = entry.getValue();
		//
		// if (tempEq.isWeapon())
		// {
		// if (tempEq.profKey(aPc).equals(profKey))
		// {
		// eq = tempEq;
		//
		// break;
		// }
		// }
		// }
		// }
		// }
		//
		// boolean isValid = false; // assume we match unless...
		//
		// if (eq != null)
		// {
		// if (typeList.size() == 0)
		// {
		// isValid = true;
		// }
		// else
		// {
		// //
		// // search all the optional type strings, just one
		// // match passes the test
		// //
		// for (Iterator<String> wpi = typeList.iterator(); wpi
		// .hasNext();)
		// {
		// final String wpString = wpi.next();
		//
		// if (eq.isType(wpString))
		// {
		// isValid = true; // if it contains even one
		// // of the TYPE strings, it
		// // passes
		//
		// break;
		// }
		// }
		// }
		// }
		//
		// if (!isValid)
		// {
		// continue;
		// }
		//
		// if (!availableList.contains(wp))
		// {
		// if ("Light".equals(sString)
		// && eq.isWeaponLightForPC(aPc))
		// {
		// availableList.add(wp);
		// }
		//
		// if ("1 handed".equals(sString)
		// && eq.isWeaponOneHanded(aPc))
		// {
		// availableList.add(wp);
		// }
		//
		// if ("2 handed".equals(sString)
		// && eq.isWeaponTwoHanded(aPc))
		// {
		// availableList.add(wp);
		// }
		// }
		// }
		// }
		// else if ("SPELLCASTER.".regionMatches(true, 0, aString, 0, 12))
		// {
		// // TODO this should not be hardcoded.
		// String profKey = aString.substring(12);
		// final WeaponProf wp = Globals.getWeaponProfKeyed(profKey);
		// if (wp == null)
		// {
		// continue;
		// }
		// if (aPc.isSpellCaster(1) && !availableList.contains(wp))
		// {
		// availableList.add(wp);
		// }
		// }
		// else if ("TYPE.".regionMatches(true, 0, aString, 0, 5)
		// || "TYPE=".regionMatches(true, 0, aString, 0, 5))
		// {
		// String sString = aString.substring(5);
		// boolean adding = true;
		// Iterator<WeaponProf> setIter = aPc.getWeaponProfs().iterator();
		//
		// if (sString.startsWith("Not."))
		// {
		// sString = sString.substring(4);
		// setIter = availableList.iterator();
		// adding = false;
		// }
		//
		// WeaponProf wp;
		// Equipment eq;
		//
		// while (setIter.hasNext())
		// {
		// wp = setIter.next();
		// if (wp == null)
		// {
		// continue;
		// }
		//
		// eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());
		//
		// if (eq == null)
		// {
		// if (!wp.isType("Natural")) // natural weapons are not
		// // in the global eq.list
		// {
		// continue;
		// }
		//
		// if (adding && !availableList.contains(wp))
		// {
		// availableList.add(wp);
		// }
		// }
		// else if (eq.typeStringContains(sString))
		// {
		// // if this item is of the desired type, add it to the
		// // list
		// if (adding && !availableList.contains(wp))
		// {
		// availableList.add(wp);
		// }
		//
		// // or try to remove it and reset the iterator since
		// // remove cause fits
		// else if (!adding
		// && availableList.contains(wp.getKeyName()))
		// {
		// availableList.remove(wp);
		// setIter = availableList.iterator();
		// }
		// }
		// else if (sString.equalsIgnoreCase("LIGHT"))
		// {
		// // if this item is of the desired type, add it to the
		// // list
		// if (adding && !availableList.contains(wp)
		// && eq.isWeaponLightForPC(aPc))
		// {
		// availableList.add(wp);
		// }
		// // or try to remove it and reset the iterator since
		// // remove cause fits
		// else if (!adding && availableList.contains(wp)
		// && eq.isWeaponLightForPC(aPc))
		// {
		// availableList.remove(wp);
		// setIter = availableList.iterator();
		// }
		// }
		// }
		// }
		//
		// return ChooseLoader.parseToken(context, WeaponProf.class, value);
		if (pcsList.size() == 1)
		{
			return pcsList.get(0);
		}
		else
		{
			return new CompoundOrChoiceSet<CDOMWeaponProf>(pcsList);
		}
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
