/*
 * PreShieldProficiency.java
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
import pcgen.core.PlayerCharacter;
import pcgen.core.ShieldProf;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author wardc
 * 
 */
public class PreShieldProfTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final int numberRequired = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;

		final String aString = prereq.getKey();
		for (String profName : character.getShieldProfList())
		{
			if (profName.equalsIgnoreCase(aString))
			{
				runningTotal++;
			}
			else if (profName.substring(5).equalsIgnoreCase(
				aString.substring(5)))
			{
				runningTotal++;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, numberRequired);
		return countedTotal(prereq, runningTotal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "SHIELDPROF"; //$NON-NLS-1$
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
			List<ShieldProf> list =
					activeGraph.getGrantedNodeList(ShieldProf.class);
			SHIELDPROF: for (ShieldProf sp : list)
			{
				StringTokenizer tok =
						new StringTokenizer(aString.substring(5), ".");
				// Must match all listed types in order to qualify
				while (tok.hasMoreTokens())
				{
					Type requiredType = Type.getConstant(tok.nextToken());
					if (!sp.containsInList(ListKey.TYPE, requiredType))
					{
						continue SHIELDPROF;
					}
				}
				runningTotal++;
			}
		}
		else
		{
			if (activeGraph.containsGranted(ShieldProf.class, aString))
			{
				runningTotal++;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, numberRequired);
		return countedTotal(prereq, runningTotal);
	}

}
