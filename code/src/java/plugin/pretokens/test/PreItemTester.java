/*
 * PreItem.java
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
import pcgen.character.CharacterDataStore;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
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
public class PreItemTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	// TODO Refactor this with all the equipment tests.
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
		throws PrerequisiteException
	{
		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException e)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreItem.error.bad_operand", prereq.toString())); //$NON-NLS-1$
		}

		int runningTotal = 0;

		if (!character.getEquipmentList().isEmpty())
		{
			// Work out exactlywhat we are going to test.
			final String aString = prereq.getKey();
			List<String> typeList = null;
			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				typeList = CoreUtility.split(aString.substring(5), '.');
			}

			for (Equipment eq : character.getEquipmentList())
			{
				if (typeList != null)
				{
					// Check to see if the equipment matches
					// all of the types in the requested list;
					boolean bMatches = true;
					for (int i = 0, x = typeList.size(); i < x; ++i)
					{
						if (!eq.isType(typeList.get(i)))
						{
							bMatches = false;
							break;
						}
					}
					if (bMatches)
					{
						runningTotal++;
					}
				}
				else
				{ // not a TYPE string
					final String eqName = eq.getName().toUpperCase();

					if (aString.indexOf('%') >= 0)
					{
						// handle wildcards (always assume
						// they end the line)
						final int percentPos = aString.indexOf('%');
						final String substring =
								aString.substring(0, percentPos).toUpperCase();
						if ((eqName.startsWith(substring)))
						{
							++runningTotal;
							break;
						}
					}
					else if (eqName.equalsIgnoreCase(aString))
					{
						// just a straight String compare
						++runningTotal;
						break;
					}
				}
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "ITEM"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character)
		throws PrerequisiteException
	{
		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException e)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreItem.error.bad_operand", prereq.toString())); //$NON-NLS-1$
		}

		int runningTotal = 0;

		/*
		 * TODO This is a RATHER interesting challenge - the problem here is
		 * that this is NOT in the active graph - it is in the complete
		 * character graph. However, there are also 'false' items that can be in
		 * that complete graph, such as a deeply embedded AUTO:EQUIP, for which
		 * the PC is not eligible... :P
		 */
		List<Equipment> list = null;
		String aString = prereq.getKey();

		for (Equipment eq : list)
		{
			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
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
					runningTotal++;
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
					// handle wildcards (always assume they end the line)
					if (eqName.regionMatches(true, 0, aString, 0, percentPos))
					{
						runningTotal++;
						break;
					}
				}
				else if (eqName.equalsIgnoreCase(aString))
				{
					// just a straight String compare
					runningTotal++;
					break;
				}
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

}
