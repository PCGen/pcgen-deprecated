/*
 * PreSpecialAbility.java
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

import pcgen.cdom.graph.PCGenGraph;
import pcgen.character.CharacterDataStore;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 */
public class PreSpecialAbilityTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
		throws PrerequisiteException
	{
		int runningTotal = 0;
		int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreSpecialAbility.error.bad_operand", prereq.toString())); //$NON-NLS-1$
		}

		final String aString = prereq.getKey().toUpperCase();
		for (SpecialAbility sa : character.getSpecialAbilityList())
		{
			if (sa.getKeyName().toUpperCase().startsWith(aString))
			{
				runningTotal++;
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "SA"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
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
				"PreTemplate.error", prereq.toString())); //$NON-NLS-1$
		}

		PCGenGraph activeGraph = character.getActiveGraph();
		List<SpecialAbility> list =
				activeGraph.getGrantedNodeList(SpecialAbility.class);
		
		if (!list.isEmpty())
		{
			String saKey = prereq.getKey().toUpperCase();
			for (SpecialAbility sa : list)
			{
				if (sa.getKeyName().toUpperCase().startsWith(saKey))
				{
					runningTotal++;
				}
			}
		}
		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

}
