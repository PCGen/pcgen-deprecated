/*
 * PreSpellCast.java
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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.character.CharacterDataStore;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 */
public class PreSpellCastTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{

		final int requiredNumber = Integer.parseInt(prereq.getOperand());
		final String prereqSpellType = prereq.getKey();
		int runningTotal = 0;

		for (PCClass aClass : character.getClassList())
		{
			if (prereqSpellType.equalsIgnoreCase(aClass.getSpellType()))
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
		return "spellcast.type"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		final Object[] args =
				new Object[]{prereq.getOperator().toDisplayString(),
					prereq.getOperand(), prereq.getKey()};
		return PropertyFactory.getFormattedString("PreSpellCast.toHtml", args); //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		final int requiredNumber = Integer.parseInt(prereq.getOperand());
		final String prereqSpellType = prereq.getKey();
		int runningTotal = 0;

		PCGenGraph activeGraph = character.getActiveGraph();
		for (CDOMPCClass cl : activeGraph.getGrantedNodeList(CDOMPCClass.class))
		{
			Type spellType = cl.get(ObjectKey.SPELL_TYPE);
			if (spellType != null
				&& prereqSpellType.equalsIgnoreCase(spellType.toString()))
			{
				runningTotal++;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

}
