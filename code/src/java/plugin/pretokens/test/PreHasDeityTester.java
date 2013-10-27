/*
 * PreHasDeity.java
 *
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
 * Created on 19-Dec-2003
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.test;

import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.character.CharacterDataStore;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author wardc
 *
 */
public class PreHasDeityTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		int runningTotal;
		final boolean charHasDeity = character.getDeity() != null;

		final String ucOp = prereq.getKey().toUpperCase();
		final boolean flag =
				(ucOp.charAt(0) == 'Y' && charHasDeity)
					|| (ucOp.charAt(0) == 'N' && !charHasDeity);
		if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
			|| prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
		{
			runningTotal = flag ? 1 : 0;
		}
		else
		{
			runningTotal = flag ? 0 : 1;
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "HAS.DEITY"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		int runningTotal;
		PCGenGraph activeGraph = character.getActiveGraph();
		boolean charHasDeity = activeGraph.getGrantedNodeCount(CDOMDeity.class) != 0;

		String ucOp = prereq.getKey().toUpperCase();
		boolean flag =
				(ucOp.charAt(0) == 'Y' && charHasDeity)
					|| (ucOp.charAt(0) == 'N' && !charHasDeity);
		if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
			|| prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
		{
			runningTotal = flag ? 1 : 0;
		}
		else
		{
			runningTotal = flag ? 0 : 1;
		}

		return countedTotal(prereq, runningTotal);
	}
}
