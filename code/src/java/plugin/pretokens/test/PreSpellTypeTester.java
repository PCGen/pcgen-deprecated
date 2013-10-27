/*
 * PreSpellType.java
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

import pcgen.character.CharacterDataStore;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 */
public class PreSpellTypeTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final String castingType = prereq.getKey();
		int requiredLevel;
		int requiredNumber;
		try
		{
			requiredLevel = Integer.parseInt(prereq.getSubKey());
		}
		catch (NumberFormatException e)
		{
			requiredLevel = 1;
			Logging
				.errorPrintLocalised(
					"PreSpellType.Badly_formed_spell_type", prereq.getSubKey(), prereq.toString()); //$NON-NLS-1$
		}

		try
		{
			requiredNumber = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException e)
		{
			requiredNumber = 1;
			Logging
				.errorPrintLocalised(
					"PreSpellType.Badly_formed_spell_type", prereq.getSubKey(), prereq.toString()); //$NON-NLS-1$
		}

		int runningTotal = 0;
		if (character.canCastSpellTypeLevel(castingType, requiredLevel,
			requiredNumber))
		{
			runningTotal = requiredNumber;
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "SPELL.TYPE"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		return PropertyFactory.getFormattedString("PreSpellType.toHtml", //$NON-NLS-1$
			new Object[]{prereq.getOperator().toDisplayString(),
				prereq.getOperand(), prereq.getKey(), prereq.getSubKey()});
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		final String castingType = prereq.getKey();
		int requiredLevel;
		int requiredNumber;
		try
		{
			requiredLevel = Integer.parseInt(prereq.getSubKey());
		}
		catch (NumberFormatException e)
		{
			requiredLevel = 1;
			Logging
				.errorPrintLocalised(
					"PreSpellType.Badly_formed_spell_type", prereq.getSubKey(), prereq.toString()); //$NON-NLS-1$
		}

		try
		{
			requiredNumber = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException e)
		{
			requiredNumber = 1;
			Logging
				.errorPrintLocalised(
					"PreSpellType.Badly_formed_spell_type", prereq.getSubKey(), prereq.toString()); //$NON-NLS-1$
		}

		int runningTotal = 0;
		if (character.canCastSpellTypeLevel(castingType, requiredLevel,
			requiredNumber))
		{
			runningTotal = requiredNumber;
		}

		return countedTotal(prereq, runningTotal);
	}
}
