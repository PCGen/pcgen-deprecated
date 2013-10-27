/*
 * PreSpellBook.java
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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.character.CharacterDataStore;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author wardc
 *
 */
public class PreSpellBookTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final boolean prereqUsesBook =
				prereq.getKey().toUpperCase().startsWith("Y"); //$NON-NLS-1$
		int runningTotal = 0;
		final int requiredNumber = Integer.parseInt(prereq.getOperand());

		for (PCClass spellClass : character.getClassList())
		{
			if (spellClass.getSpellBookUsed() == prereqUsesBook)
			{
				runningTotal++;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "SPELLBOOK"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		boolean prereqUsesBook = prereq.getKey().toUpperCase().startsWith("Y"); //$NON-NLS-1$
		int runningTotal = 0;
		int requiredNumber = Integer.parseInt(prereq.getOperand());

		PCGenGraph activeGraph = character.getActiveGraph();
		List<CDOMPCClass> list = activeGraph.getGrantedNodeList(CDOMPCClass.class);
		for (CDOMPCClass spellClass : list)
		{
			Boolean sb = spellClass.get(ObjectKey.SPELLBOOK);
			if (sb != null && sb.booleanValue() == prereqUsesBook)
			{
				runningTotal++;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

}
