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
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreHPTest</code> tests that the PREHP tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreHPTest extends AbstractCharacterTestCase
{
	PCClass myClass = new PCClass();

	public static void main(final String[] args)
	{
		TestRunner.run(PreHPTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreHPTest.class);
	}

	/**
	 * Test the PREHP code
	 * @throws Exception
	 */
	public void testHP() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		character.incrementClassLevel(1, myClass, true);
		myClass = character.getClassList().get(0);
		myClass.setHitPoint(1, Integer.valueOf(4));

		character.calcActiveBonuses();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREHP:4");

		assertTrue("Character should have 4 hp", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREHP:5");

		assertFalse("Character should have less than 5 hp", PrereqHandler
			.passes(prereq, character, null));

		final BonusObj hpBonus = Bonus.newBonus("HP|CURRENTMAX|1");
		hpBonus.setCreatorObject(myClass);
		myClass.addBonusList(hpBonus);
		character.calcActiveBonuses();

		assertTrue("Character should have 5 hp", PrereqHandler.passes(prereq,
			character, null));
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		myClass.setName("My Class");
		myClass.setAbbrev("Myc");
		myClass.setSkillPointFormula("3");
		Globals.getClassList().add(myClass);
	}
}
