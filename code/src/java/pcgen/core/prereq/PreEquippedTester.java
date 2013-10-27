/*
 * PreEquipped.java
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
package pcgen.core.prereq;

import java.util.StringTokenizer;

import pcgen.cdom.character.EquipmentSetFacade;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.character.CharacterDataStore;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.WieldCategory;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 */
public abstract class PreEquippedTester extends AbstractPrerequisiteTest
{

	/**
	 * Process the tokens and return the number that is not passed.
	 *
	 * @param prereq
	 * @param character The pc to use.
	 * @param equippedType The equipped type to look for (e.g. Equipment.EQUIPPED_TWO_HANDS)
	 *
	 * @return the number that did not pass
	 * @throws PrerequisiteException
	 */
	public int passesPreEquipHandleTokens(final Prerequisite prereq, final PlayerCharacter character, final int equippedType) throws PrerequisiteException
	{
		// TODO refactor this code with PreEquipTester
		boolean isEquipped = false;

		if (!character.getEquipmentList().isEmpty())
		{
			final String aString = prereq.getKey();
			for ( Equipment eq : character.getEquipmentList() )
			{
				//
				// Only check equipment of the type we are interested in
				//
				if  (!eq.isEquipped() || (eq.getLocation() != equippedType))
				{
					continue;
				}

				if (aString.startsWith("WIELDCATEGORY=") || aString.startsWith("WIELDCATEGORY."))
				{
					final WieldCategory wCat = eq.getEffectiveWieldCategory(character);
					if ((wCat != null) && wCat.getName().equalsIgnoreCase(aString.substring(14)))
					{
						isEquipped = true;
						break;
					}
				}
				else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))	//$NON-NLS-1$ //$NON-NLS-2$
				{
					StringTokenizer tok = new StringTokenizer(aString.substring(5), ".");
					boolean match = false;
					if (tok.hasMoreTokens())
					{
						match = true;
					}
					//
					// Must match all listed types in order to qualify
					//
					while(tok.hasMoreTokens())
					{
						final String type = tok.nextToken();
						if (!eq.isType(type))
						{
							match = false;
							break;
						}
					}
					if (match)
					{
						isEquipped = true;
						break;
					}
				}
				else	 //not a TYPE string
				{
					final String eqName = eq.getName();
					if (aString.indexOf('%') >= 0)
					{
						//handle wildcards (always assume they
						// end the line)
						final int percentPos = aString.indexOf('%');
						if (eqName.regionMatches(true, 0, aString, 0, percentPos))
						{
							isEquipped = true;
							break;
						}
					}
					else if (eqName.equalsIgnoreCase(aString))
					{
						//just a straight String compare
						isEquipped = true;
						break;
					}
				}
			}
		}

		final PrerequisiteOperator operator = prereq.getOperator();

		int runningTotal;
		if (operator.equals(PrerequisiteOperator.EQ) || operator.equals(PrerequisiteOperator.GTEQ))
		{
			runningTotal = isEquipped ? 1 : 0;
		}
		else if (operator.equals(PrerequisiteOperator.NEQ))
		{
			runningTotal = isEquipped ? 0 : 1;
		}
		else
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
					"PreEquipped.error.invalid_comparison", prereq.toString())); //$NON-NLS-1$
		}

		return countedTotal(prereq, runningTotal);

	}

	/**
	 * Process the tokens and return the number that is not passed.
	 * 
	 * @param prereq
	 * @param character
	 *            The pc to use.
	 * @param equippedType
	 *            The equipped type to look for (e.g.
	 *            Equipment.EQUIPPED_TWO_HANDS)
	 * 
	 * @return the number that did not pass
	 * @throws PrerequisiteException
	 */
	public int passesCDOMPreEquipHandleTokens(final Prerequisite prereq,
		final CharacterDataStore character, final int equippedType)
		throws PrerequisiteException
	{
		EquipmentSetFacade set = character.getEquipped();
		boolean isEquipped = false;

		if (set != null)
		{
			String aString = prereq.getKey();

			for (CDOMEquipment eq : set.getEquipment(equippedType))
			{
				if (aString.startsWith("WIELDCATEGORY=")
					|| aString.startsWith("WIELDCATEGORY."))
				{
					WieldCategory wCat = eq.getEffectiveWieldCategory(character);
					if ((wCat != null)
						&& wCat.getName().equalsIgnoreCase(aString.substring(14)))
					{
						isEquipped = true;
						break;
					}
				}
				else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					StringTokenizer tok =
						new StringTokenizer(aString.substring(5), ".");
					boolean match = true;
					//
					// Must match all listed types in order to qualify
					//
					while (tok.hasMoreTokens())
					{
						Type requiredType = Type.getConstant(tok.nextToken());
						if (!eq.containsInList(ListKey.TYPE, requiredType))
						{
							match = false;
							break;
						}
					}
					if (match)
					{
						isEquipped = true;
						break;
					}
				}
				else
				// not a TYPE string
				{
					String eqName = eq.getKeyName();
					int percentPos = aString.indexOf('%');
					if (percentPos >= 0)
					{
						// handle wildcards (always assume they
						// end the line)
						if (eqName.regionMatches(true, 0, aString, 0, percentPos))
						{
							isEquipped = true;
							break;
						}
					}
					else if (eqName.equalsIgnoreCase(aString))
					{
						// just a straight String compare
						isEquipped = true;
						break;
					}
				}
			}
		}
		PrerequisiteOperator operator = prereq.getOperator();

		int runningTotal;
		if (operator.equals(PrerequisiteOperator.EQ)
			|| operator.equals(PrerequisiteOperator.GTEQ))
		{
			runningTotal = isEquipped ? 1 : 0;
		}
		else if (operator.equals(PrerequisiteOperator.NEQ))
		{
			runningTotal = isEquipped ? 0 : 1;
		}
		else
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreEquipped.error.invalid_comparison", prereq.toString())); //$NON-NLS-1$
		}

		return countedTotal(prereq, runningTotal);
	}
}
