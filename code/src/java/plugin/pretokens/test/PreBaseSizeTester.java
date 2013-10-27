/*
 * PreBaseSize.java
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

import pcgen.base.formula.Resolver;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.CDOMRace;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.character.CharacterDataStore;
import pcgen.core.Globals;
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
public class PreBaseSizeTester extends AbstractPrerequisiteTest implements
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

		if ((character.getRace() != null)
			&& !character.getRace().equals(Globals.s_EMPTYRACE))
		{
			final String key =
					String.valueOf(prereq.getOperand().toUpperCase().charAt(0));
			final int targetSize = Globals.sizeInt(key, -1);
			if (targetSize < 0)
			{
				throw new PrerequisiteException(PropertyFactory
					.getFormattedString(
						"PreBaseSize.error.bad_size", prereq.getOperand())); //$NON-NLS-1$
			}
			runningTotal =
					prereq.getOperator().compare(character.racialSizeInt(),
						targetSize);
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "BASESIZE"; //$NON-NLS-1$
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		int runningTotal = 0;
		PCGenGraph activeGraph = character.getActiveGraph();
		List<CDOMRace> list = activeGraph.getGrantedNodeList(CDOMRace.class);
		String sizeAbb = prereq.getOperand().toUpperCase();
		CDOMSizeAdjustment requiredSize = character.getRulesData().getObject(
				CDOMSizeAdjustment.class, sizeAbb);
		int reqSizeOrdinal = requiredSize.getOrdinal();

		for (CDOMRace r : list)
		{
			Resolver<CDOMSizeAdjustment> raceSize = r.get(ObjectKey.SIZE);
			runningTotal =
					prereq.getOperator().compare(
						raceSize.resolve().getOrdinal(), reqSizeOrdinal);
		}

		return countedTotal(prereq, runningTotal);
	}

}
