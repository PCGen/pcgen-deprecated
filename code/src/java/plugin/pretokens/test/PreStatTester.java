/*
 * PreStat.java
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
import pcgen.cdom.inst.CDOMStat;
import pcgen.character.CharacterDataStore;
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
public class PreStatTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final int targetNumber = Integer.parseInt(prereq.getOperand());
		final int statValue =
				character.getStatList().getTotalStatFor(prereq.getKey());

		final int runningTotal =
				prereq.getOperator().compare(statValue, targetNumber);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "STAT"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		return PropertyFactory
			.getFormattedString(
				"PreStat.toHtml", prereq.getKey(), prereq.getOperator().toString(), prereq.getOperand()); //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		int targetNumber = Integer.parseInt(prereq.getOperand());
		PCGenGraph graph = character.getActiveGraph();
		CDOMStat stat = graph.getGrantedNode(CDOMStat.class, prereq.getKey());
		int statVal = character.getTotalWeight(stat);
		int runningTotal = prereq.getOperator().compare(statVal, targetNumber);
		return countedTotal(prereq, runningTotal);
	}

}
