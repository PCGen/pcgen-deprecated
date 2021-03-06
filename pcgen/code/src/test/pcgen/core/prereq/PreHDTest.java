/*
 * PreHDTest.java
 *
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreHDTest</code> tests that the PREHD tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
@SuppressWarnings("nls")
public class PreHDTest extends AbstractCharacterTestCase
{
	Race race = new Race();
	Race race1 = new Race();
	PCClass monClass = new PCClass();

	/**
	 * Main method.  Runs the test.
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreHDTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreHDTest.class);
	}

	/**
	 * Test the PREHD code
	 * @throws Exception
	 */
	public void testHD() throws Exception
	{
		race.setName("Human");
		race.put(FormulaKey.SIZE, new FixedSizeFormula(medium));
		Globals.getContext().getReferenceContext().importObject(race);

		PCClass raceClass = new PCClass();
		raceClass.setName("Race Class");
		raceClass.put(StringKey.KEY_NAME, "RaceClass");
		raceClass.put(ObjectKey.IS_MONSTER, true);
		Globals.getContext().getReferenceContext().importObject(raceClass);

		race.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(
				CDOMDirectSingleRef.getRef(raceClass), FormulaFactory
						.getFormulaFor(3)));

		final PlayerCharacter character = getCharacter();
		character.setRace(race);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREHD:MIN=4");

		assertFalse("Character doesn't have 4 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=3");

		assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=1,MAX=3");

		assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=3,MAX=6");

		assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=4,MAX=7");

		assertFalse("Character doesn't have 4 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=1,MAX=2");

		assertFalse("Character doesn't have 2 or less HD", PrereqHandler
			.passes(prereq, character, null));
	}

	/**
	 * Tests using monster class levels
	 * @throws Exception
	 */
	public void testClassLevels() throws Exception
	{
		monClass.setName("Humanoid");
		monClass.put(ObjectKey.IS_MONSTER, true);
		Globals.getContext().getReferenceContext().importObject(monClass);

		race1.setName("Bugbear");
		race1.put(FormulaKey.SIZE, new FixedSizeFormula(large));

		race1.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(
				CDOMDirectSingleRef.getRef(monClass), FormulaFactory
						.getFormulaFor(3)));
		Globals.getContext().getReferenceContext().importObject(race1);

		final PlayerCharacter character = new PlayerCharacter();
		character.setRace(race1);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREHD:MIN=4");

		assertFalse("Character doesn't have 4 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=3");

		assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=1,MAX=3");

		assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=3,MAX=6");

		assertTrue("Character has 3 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=4,MAX=7");

		assertFalse("Character doesn't have 4 HD", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHD:MIN=1,MAX=2");

		assertFalse("Character doesn't have 2 or less HD", PrereqHandler
			.passes(prereq, character, null));
	}
}
