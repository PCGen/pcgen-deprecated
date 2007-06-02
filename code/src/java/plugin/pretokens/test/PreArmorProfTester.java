/*
 * PreArmourProficiency.java
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
import pcgen.core.ArmorProf;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author wardc
 *
 */
public class PreArmorProfTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		int runningTotal = 0;

		for (String profName : character.getArmorProfList())
		{
			if (profName.equalsIgnoreCase(prereq.getKey()))
			{
				runningTotal++;
			}
			else if (profName.substring(5).equalsIgnoreCase(prereq.getKey()))
			{
				// TYPE=Light equals TYPE.Light
				runningTotal++;
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "armorprof"; //$NON-NLS-1$
	}

		public int passesCDOM(Prerequisite prereq, PlayerCharacter character)
		throws PrerequisiteException
	{
		final int numberRequired = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;

		PCGenGraph activeGraph = character.getActiveGraph();
		final String aString = prereq.getKey();
		if (aString.startsWith("TYPE.") || aString.startsWith("TYPE=")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			final String requiredType = aString.substring(5);
			List<ArmorProf> list =
					activeGraph.getGrantedNodeList(ArmorProf.class);
			for (ArmorProf ap : list)
			{
				if (ap.isType(requiredType))
				{
					runningTotal++;
				}
			}
		}
		else
		{
			if (activeGraph.containsGranted(ArmorProf.class, aString))
			{
				runningTotal++;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, numberRequired);
		return countedTotal(prereq, runningTotal);
	}

}
