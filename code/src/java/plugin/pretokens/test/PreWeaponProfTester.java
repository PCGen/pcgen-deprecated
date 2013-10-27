/*
 * PreWeaponProficiency.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.test;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.character.CharacterDataStore;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.utils.CoreUtility;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 * 
 */
public class PreWeaponProfTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/**
	 * <b>Tag Name</b>: <code>PREWEAPONPROF:x,y,y</code><br />
	 * &nbsp; <b>Variables Used (x)</b>: <i>Number</i> (The number of
	 * proficiencies that must match the specified requirements). <br/> &nbsp;
	 * <b>Variables Used (y)</b>: <i>Text</i> (The name of a weapon
	 * proficiency). <br />
	 * &nbsp; <b>Variables Used (y)</b>: <code>TYPE.</code><i>Text</i> (The
	 * name of a weaponprof type). <br />
	 * &nbsp; <b>Variables Used (y)</b>: <code>DEITYWEAPON</code> (The
	 * favored weapon of the character's deity). <br />
	 * <p />
	 * <b>What it does:</b><br />
	 * &nbsp; Sets weapon proficiency requirements.
	 * <p />
	 * <b>Examples</b>: <br/> &nbsp; <code>PREWEAPONPROF:2,Kama,Katana</code><br />
	 * &nbsp; &nbsp; Character must have both "Kama" and "Katana".
	 * <p />
	 * &nbsp; <code>PREWEAPONPROF:1,TYPE.Exotic</code> <br />
	 * &nbsp; &nbsp; Character must have proficiency with any one exotic
	 * weaponprof type.
	 * <p />
	 * &nbsp; <code>PREWEAPONPROF:1,TYPE.Martial,Chain (Spiked)</code> <br />
	 * &nbsp; &nbsp; Character must have proficiency with either the Chain
	 * (Spiked) or any martial weapon.
	 * <p />
	 * &nbsp; <code>PREWEAPONPROF:1,DEITYWEAPON</code> <br />
	 * &nbsp; &nbsp; Weapon Prof in question must be one of the chosen deity's
	 * favored weapons.
	 * 
	 * @see pcgen.core.prereq.AbstractPrerequisiteTest#passes(pcgen.core.prereq.Prerequisite,
	 *      pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
		throws PrerequisiteException
	{
		int runningTotal = 0;

		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreFeat.error", prereq.toString())); //$NON-NLS-1$
		}

		final String aString = prereq.getKey();
		if ("DEITYWEAPON".equals(aString) && character.getDeity() != null) //$NON-NLS-1$
		{
			for (String weaponKey : CoreUtility.split(character.getDeity()
				.getFavoredWeapon(), '|'))
			{
				if (character.hasWeaponProfKeyed(weaponKey))
				{
					runningTotal++;
				}
			}
		}
		else if (aString.startsWith("TYPE.") || aString.startsWith("TYPE=")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			final String requiredType = aString.substring(5);
			for (WeaponProf wp : character.getWeaponProfs())
			{
				if (wp.isType(requiredType))
				{
					runningTotal++;
				}
				else
				{
					final Equipment eq =
							EquipmentList.getEquipmentNamed(wp.getKeyName());
					if (eq != null)
					{
						if (eq.isType(requiredType))
						{
							runningTotal++;
						}
					}
				}
			}
		}
		else
		{
			if (character.hasWeaponProfKeyed(aString))
			{
				runningTotal++;
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * @see pcgen.core.prereq.PrerequisiteTest#kindHandled()
	 */
	public String kindHandled()
	{
		return "WEAPONPROF"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character)
		throws PrerequisiteException
	{
		int runningTotal = 0;

		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreFeat.error", prereq.toString())); //$NON-NLS-1$
		}

		PCGenGraph activeGraph = character.getActiveGraph();
		final String aString = prereq.getKey();
		if ("DEITYWEAPON".equals(aString) && character.getCDOMDeity() != null) //$NON-NLS-1$
		{
			for (String weaponKey : CoreUtility.split(character.getCDOMDeity()
				.getFavoredWeapon(), '|'))
			{
				if (activeGraph.containsGranted(CDOMWeaponProf.class, weaponKey))
				{
					runningTotal++;
				}
			}
		}
		else if (aString.startsWith("TYPE.") || aString.startsWith("TYPE=")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			List<CDOMWeaponProf> list =
					activeGraph.getGrantedNodeList(CDOMWeaponProf.class);
			if (list != null)
			{
				WEAPONPROF: for (CDOMWeaponProf wp : list)
				{
					StringTokenizer tok =
							new StringTokenizer(aString.substring(5), ".");
					// Must match all listed types in order to qualify
					while (tok.hasMoreTokens())
					{
						Type requiredType = Type.getConstant(tok.nextToken());
						if (!wp.containsInList(ListKey.TYPE, requiredType))
						{
							CDOMEquipment eq = character.getRulesData()
									.getObject(CDOMEquipment.class,
											wp.getKeyName());
							if (eq != null)
							{
								StringTokenizer eqtok =
										new StringTokenizer(aString
											.substring(5), ".");
								// Must match all listed types in order to
								// qualify
								while (eqtok.hasMoreTokens())
								{
									Type requiredEqType =
											Type.getConstant(tok.nextToken());
									if (!eq.containsInList(ListKey.TYPE,
										requiredEqType))
									{
										continue WEAPONPROF;
									}
								}
							}
							continue WEAPONPROF;
						}
					}
					runningTotal++;
				}
			}
		}
		else
		{
			if (activeGraph.containsGranted(CDOMWeaponProf.class, aString))
			{
				runningTotal++;
			}
		}
		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

}
