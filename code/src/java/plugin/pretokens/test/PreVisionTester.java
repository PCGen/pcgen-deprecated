/*
 * PreVision.java
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

import pcgen.cdom.graph.PCGenGraph;
import pcgen.character.CharacterDataStore;
import pcgen.core.PlayerCharacter;
import pcgen.core.Vision;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.enumeration.VisionType;

/**
 * @author wardc
 *
 * Checks a characters vision..
 */
public class PreVisionTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final int requiredRange = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;
		VisionType requiredVisionType =
				VisionType.getVisionType(prereq.getKey());

		boolean found = false;
		for (Vision charVision : character.getVisionList())
		{
			if (charVision.getType().equals(requiredVisionType))
			{
				int visionRange = Integer.parseInt(charVision.getDistance());
				runningTotal +=
						prereq.getOperator()
							.compare(visionRange, requiredRange);
				found = true;
				break;
			}
		}
		if (!found)
		{
			runningTotal += prereq.getOperator().compare(0, requiredRange);
		}
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "VISION"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		final int requiredRange = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;
		VisionType requiredVisionType =
				VisionType.getVisionType(prereq.getKey());

		PCGenGraph activeGraph = character.getActiveGraph();

		for (Vision charVision : activeGraph.getGrantedNodeList(Vision.class))
		{
			if (charVision.getType().equals(requiredVisionType))
			{
				int visionRange = Integer.parseInt(charVision.getDistance());
				runningTotal +=
						prereq.getOperator()
							.compare(visionRange, requiredRange);
			}
		}
		return countedTotal(prereq, runningTotal);
	}

}
