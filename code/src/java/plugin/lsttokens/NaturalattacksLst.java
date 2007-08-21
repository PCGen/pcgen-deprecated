/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMAddressedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class NaturalattacksLst implements GlobalLstToken
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "NATURALATTACKS"; //$NON-NLS-1$
	}

	/**
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject,
	 *      java.lang.String, int)
	 */
	public boolean parse(PObject obj, String value, int anInt)
	{
		// first entry is primary, others are secondary
		// lets try the format:
		// NATURALATTACKS:primary weapon name,num attacks,damage|secondary1
		// weapon
		// name,num attacks,damage|secondary2.....
		// damage will be of the form XdY+Z or XdY-Z
		List<Equipment> naturalWeapons = parseNaturalAttacks(obj, value);
		for (Equipment weapon : naturalWeapons)
		{
			obj.addNaturalWeapon(weapon, anInt);
		}
		return true;
	}

	/**
	 * NATURAL WEAPONS CODE <p/>first natural weapon is primary, the rest are
	 * secondary; NATURALATTACKS:primary weapon name,weapon type,num
	 * attacks,damage|secondary1 weapon name,weapon type,num
	 * attacks,damage|secondary2 format is exactly as it would be in an
	 * equipment lst file Type is of the format Weapon.Natural.Melee.Bludgeoning
	 * number of attacks is the number of attacks with that weapon at BAB (for
	 * primary), or BAB - 5 (for secondary)
	 * 
	 * @param obj
	 * @param aString
	 * @return List
	 */
	private static List<Equipment> parseNaturalAttacks(PObject obj,
		String aString)
	{
		// Currently, this isn't going to work with monk attacks
		// - their unarmed stuff won't be affected.
		String aSize = "M";

		if (obj instanceof PCTemplate)
		{
			aSize = ((PCTemplate) obj).getTemplateSize();
		}
		else if (obj instanceof Race)
		{
			aSize = ((Race) obj).getSize();
		}

		if (aSize == null)
		{
			aSize = "M";
		}

		int count = 1;
		boolean onlyOne = false;

		final StringTokenizer attackTok = new StringTokenizer(aString, "|");

		// Make a preliminary guess at whether this is an "only" attack
		if (attackTok.countTokens() == 1)
		{
			onlyOne = true;
		}

		// This is wrong as we need to replace old natural weapons
		// with "better" ones
		List<Equipment> naturalWeapons = new ArrayList<Equipment>();

		while (attackTok.hasMoreTokens())
		{
			StringTokenizer aTok =
					new StringTokenizer(attackTok.nextToken(), ",");
			Equipment anEquip = createNaturalWeapon(obj, aTok, aSize);

			if (anEquip != null)
			{
				if (count == 1)
				{
					anEquip.setModifiedName("Natural/Primary");
				}
				else
				{
					anEquip.setModifiedName("Natural/Secondary");
				}

				if (onlyOne && anEquip.isOnlyNaturalWeapon())
				{
					anEquip.setOnlyNaturalWeapon(true);
				}
				else
				{
					anEquip.setOnlyNaturalWeapon(false);
				}

				anEquip.setOutputIndex(0);
				anEquip.setOutputSubindex(count);
				naturalWeapons.add(anEquip);
			}

			count++;
		}
		return naturalWeapons;
	}

	/**
	 * Create the Natural weapon equipment item aTok = primary weapon
	 * name,weapon type,num attacks,damage for Example:
	 * Tentacle,Weapon.Natural.Melee.Slashing,*4,1d6
	 * 
	 * @param aTok
	 * @param aSize
	 * @return natural weapon
	 */
	private static Equipment createNaturalWeapon(PObject obj,
		StringTokenizer aTok, String aSize)
	{
		final String attackName = aTok.nextToken();

		if (attackName.equalsIgnoreCase(Constants.LST_NONE))
		{
			return null;
		}

		Equipment anEquip = new Equipment();
		final String profType = aTok.nextToken();

		anEquip.setName(attackName);
		anEquip.setKeyName(obj.getClass().getSimpleName() + "," + obj.getKey()
			+ "," + attackName);
		anEquip.setTypeInfo(profType);
		anEquip.setWeight("0");
		anEquip.setSize(aSize, true);

		String numAttacks = aTok.nextToken();
		boolean attacksProgress = true;

		if ((numAttacks.length() > 0) && (numAttacks.charAt(0) == '*'))
		{
			numAttacks = numAttacks.substring(1);
			attacksProgress = false;
		}

		int bonusAttacks = 0;

		try
		{
			bonusAttacks = Integer.parseInt(numAttacks) - 1;
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Non-numeric value for number of attacks: '"
				+ numAttacks + "'");
		}

		if (bonusAttacks > 0)
		{
			anEquip.addBonusList("WEAPON|ATTACKS|" + bonusAttacks);
			anEquip.setOnlyNaturalWeapon(false);
		}
		else
		{
			anEquip.setOnlyNaturalWeapon(true);
		}

		anEquip.setDamage(aTok.nextToken());
		anEquip.setCritRange("1");
		anEquip.setCritMult(2);
		anEquip.setProfName(attackName);

		// sage_sam 02 Dec 2002 for Bug #586332
		// allow hands to be required to equip natural weapons
		int handsRequired = 0;

		if (aTok.hasMoreTokens())
		{
			final String hString = aTok.nextToken();

			try
			{
				handsRequired = Integer.parseInt(hString);
			}
			catch (NumberFormatException exc)
			{
				Logging.errorPrint("Non-numeric value for hands required: '"
					+ hString + "'");
			}
		}

		anEquip.setSlots(handsRequired);

		// these values need to be locked.
		anEquip.setQty(new Float(1));
		anEquip.setNumberCarried(new Float(1));
		anEquip.setAttacksProgress(attacksProgress);

		// Check if the proficiency needs created
		WeaponProf prof = Globals.getWeaponProfKeyed(attackName);

		if (prof == null)
		{
			prof = new WeaponProf();
			prof.setTypeInfo(profType);
			prof.setName(attackName);
			prof.setKeyName(attackName);
			Globals.addWeaponProf(prof);
		}

		anEquip.addAutoArray("WEAPONPROF", attackName); //$NON-NLS-1$
		return anEquip;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		// first entry is primary, others are secondary
		// lets try the format:
		// NATURALATTACKS:primary weapon name,num attacks,damage|secondary1
		// weapon
		// name,num attacks,damage|secondary2.....
		// damage will be of the form XdY+Z or XdY-Z
		List<Equipment> naturalWeapons =
				parseNaturalAttacks(context, obj, value);
		if (naturalWeapons == null)
		{
			return false;
		}
		for (Equipment weapon : naturalWeapons)
		{
			context.getGraphContext().grant(getTokenName(), obj, weapon);
		}
		return true;
	}

	/**
	 * NATURAL WEAPONS CODE <p/>first natural weapon is primary, the rest are
	 * secondary; NATURALATTACKS:primary weapon name,weapon type,num
	 * attacks,damage|secondary1 weapon name,weapon type,num
	 * attacks,damage|secondary2 format is exactly as it would be in an
	 * equipment lst file Type is of the format Weapon.Natural.Melee.Bludgeoning
	 * number of attacks is the number of attacks with that weapon at BAB (for
	 * primary), or BAB - 5 (for secondary)
	 * 
	 * @param obj
	 * @param aString
	 * @return List
	 */
	private List<Equipment> parseNaturalAttacks(LoadContext context,
		CDOMObject obj, String aString)
	{
		// Currently, this isn't going to work with monk attacks
		// - their unarmed stuff won't be affected.

		/*
		 * This does not immediately resolve the Size, because it is an order of
		 * operations issue. This token must allow the SIZE token to appear
		 * AFTER this token in the LST file. Thus a deferred resolution (using a
		 * Resolver) is required.
		 */
		CDOMAddressedSingleRef<SizeAdjustment> size =
				context.ref.getCDOMAddressedReference(obj,
					SizeAdjustment.class, "Size Adjustment");

		int count = 1;
		final StringTokenizer attackTok = new StringTokenizer(aString, "|");

		// This is wrong as we need to replace old natural weapons
		// with "better" ones
		List<Equipment> naturalWeapons = new ArrayList<Equipment>();

		while (attackTok.hasMoreTokens())
		{
			String tokString = attackTok.nextToken();
			Equipment anEquip =
					createNaturalWeapon(context, obj, tokString, size);

			if (anEquip == null)
			{
				Logging.errorPrint("Natural Weapon Creation Failed for : "
					+ tokString);
				return null;
			}

			if (count == 1)
			{
				anEquip.put(StringKey.MODIFIED_NAME, "Natural/Primary");
			}
			else
			{
				anEquip.put(StringKey.MODIFIED_NAME, "Natural/Secondary");
			}

			anEquip.put(IntegerKey.OUTPUT_INDEX, Integer.valueOf(0));
			anEquip.put(IntegerKey.OUTPUT_SUBINDEX, Integer.valueOf(count));
			naturalWeapons.add(anEquip);

			count++;
		}
		return naturalWeapons;
	}

	/**
	 * Create the Natural weapon equipment item aTok = primary weapon
	 * name,weapon type,num attacks,damage for Example:
	 * Tentacle,Weapon.Natural.Melee.Slashing,*4,1d6
	 * 
	 * @param aTok
	 * @param size
	 * @return natural weapon
	 */
	private Equipment createNaturalWeapon(LoadContext context, CDOMObject obj,
		String wpn, CDOMAddressedSingleRef<SizeAdjustment> size)
	{
		StringTokenizer commaTok = new StringTokenizer(wpn, Constants.COMMA);

		if (commaTok.countTokens() != 4)
		{
			Logging.errorPrint("Invalid Build of " + "Natural Weapon in "
				+ getTokenName() + ": " + wpn);
			return null;
		}

		String attackName = commaTok.nextToken();

		if (attackName.equalsIgnoreCase(Constants.LST_NONE))
		{
			Logging.errorPrint("Attempt to Build 'None' as a "
				+ "Natural Weapon in " + getTokenName() + ": " + wpn);
			return null;
		}

		Equipment anEquip = new Equipment();
		anEquip.setName(attackName);
		/*
		 * This really can't be raw equipment... It really never needs to be
		 * referred to, but this means that duplicates are never being detected
		 * and resolved... this needs to have a KEY defined, to keep it
		 * unique... hopefully this is good enough :)
		 */
		anEquip.setKeyName(obj.getClass().getSimpleName() + ","
			+ obj.getKeyName() + "," + attackName);
		/*
		 * Perhaps the construction above should be through context just to
		 * guarantee uniqueness of the key?? - that's too paranoid
		 */

		String profType = commaTok.nextToken();
		String numAttacks = commaTok.nextToken();

		boolean attacksProgress = true;
		if ((numAttacks.length() > 0) && (numAttacks.charAt(0) == '*'))
		{
			numAttacks = numAttacks.substring(1);
			attacksProgress = false;
		}

		int bonusAttacks = 0;
		try
		{
			bonusAttacks = Integer.parseInt(numAttacks) - 1;
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Non-numeric value for number of attacks in "
				+ getTokenName() + ": '" + numAttacks + "'");
			return null;
		}

		String damage = commaTok.nextToken();

		// sage_sam 02 Dec 2002 for Bug #586332
		// allow hands to be required to equip natural weapons
		int handsRequired = 0;

		if (commaTok.hasMoreTokens())
		{
			final String hString = commaTok.nextToken();

			try
			{
				handsRequired = Integer.parseInt(hString);
			}
			catch (NumberFormatException exc)
			{
				Logging.errorPrint("Non-numeric value for hands required: '"
					+ hString + "'");
				return null;
			}
		}

		StringTokenizer dotTok = new StringTokenizer(profType, Constants.DOT);
		while (dotTok.hasMoreTokens())
		{
			/*
			 * FUTURE How are types made type safe?
			 */
			// WeaponType wt = WeaponType.getConstant(dotTok.nextToken());
			Type wt = Type.getConstant(dotTok.nextToken());
			anEquip.addToListFor(ListKey.TYPE, wt);
		}

		anEquip.put(ObjectKey.WEIGHT, BigDecimal.ZERO);

		if (bonusAttacks > 0)
		{
			// TODO FIXME anEquip.addBonusList("WEAPON|ATTACKS|" +
			// bonusAttacks);
		}

		EquipmentHead equipHead = new EquipmentHead(anEquip, 1);
		equipHead.put(StringKey.DAMAGE, damage);
		equipHead.put(IntegerKey.CRIT_RANGE, Integer.valueOf(1));
		equipHead.put(IntegerKey.CRIT_MULT, Integer.valueOf(2));

		context.ref.constructIfNecessary(WEAPONPROF_CLASS, attackName);
		CDOMSimpleSingleRef<WeaponProf> wp =
				context.ref.getCDOMReference(WEAPONPROF_CLASS, attackName);
		anEquip.put(ObjectKey.WEAPON_PROF, wp);
		context.getGraphContext().grant(getTokenName(), anEquip, wp);

		anEquip.put(IntegerKey.SLOTS, Integer.valueOf(handsRequired));

		// TODO FIXME these values need to be locked (how precisely is this
		// done?)
		// I think Quantity and number carried are actually PCGenGraph locks,
		// not
		// inherent to the Equipment itself...

		// TODO uncomment
		// anEquip.setQty(new Float(1));
		// anEquip.setNumberCarried(new Float(1));
		anEquip.put(ObjectKey.ATTACKS_PROGRESS, Boolean
			.valueOf(attacksProgress));

		/*
		 * TODO FIXME I'm almost convinced that Equipment CANNOT be done with
		 * link* or allow*, as the general relationships are contains and
		 * equips?
		 */
		context.getGraphContext().grant(getTokenName(), anEquip, size);
		context.getGraphContext().grant(Constants.VT_EQ_HEAD, anEquip,
			equipHead);
		return anEquip;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
