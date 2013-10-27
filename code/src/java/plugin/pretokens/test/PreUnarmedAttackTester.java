/*
 * PreUnarmedAttack.java
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

import java.util.StringTokenizer;

import pcgen.character.CharacterDataStore;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.AttackType;

/**
 * @author wardc
 *
 */
public class PreUnarmedAttackTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		int att = 0;
		for (PCClass aClass : character.getClassList())
		{
			String s = aClass.getUattForLevel(aClass.getLevel());
			if (s.length() == 0 || "0".equals(s)) //$NON-NLS-1$
			{
				att = Math.max(att, aClass.baseAttackBonus(character));
			}
			else
			{
				final StringTokenizer bTok = new StringTokenizer(s, ","); //$NON-NLS-1$
				s = bTok.nextToken();
				try
				{
					att = Math.max(att, Integer.parseInt(s));
				}
				catch (NumberFormatException e)
				{
					Logging.errorPrint(PropertyFactory.getFormattedString(
						"PreUnarmedAttack.error.bad_operand", s)); //$NON-NLS-1$
				}
			}
		}

		final int requiredValue = Integer.parseInt(prereq.getOperand());
		final int runningTotal =
				prereq.getOperator().compare(att, requiredValue);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "UATT"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		return PropertyFactory
			.getFormattedString(
				"PreUnarmedAttack.toHtml", prereq.getOperator().toString(), prereq.getOperand()); //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		int requiredValue = Integer.parseInt(prereq.getOperand());
		int att = character.getAttackCount(AttackType.UNARMED);
		int runningTotal = prereq.getOperator().compare(att, requiredValue);
		return countedTotal(prereq, runningTotal);
	}
}
