/*
 * PreBaseSizeTest.java
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
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreBaseSizeTest</code> tests that the PREBASESIZE tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
@SuppressWarnings("nls")
public class PreBaseSizeTest extends AbstractCharacterTestCase
{
	Race race = new Race();
	PCTemplate template = new PCTemplate();

	public static void main(final String[] args)
	{
		TestRunner.run(PreBaseSizeTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreBaseSizeTest.class);
	}

	/**
	 * Test the PREBASESIZE code
	 * @throws Exception
	 */
	public void testBaseSize() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setRace(race);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREBASESIZEEQ:Medium");

		assertTrue("Character's base size should be equal to Medium",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZELTEQ:Medium");

		assertTrue("Character's base size should be <= to Medium",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZEGTEQ:Medium");

		assertTrue("Character's base size should be >= to Medium",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZEGT:Small");

		assertTrue("Character's base size should be > to Small", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZELT:Large");

		assertTrue("Character's base size should be < to Large", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZELT:Medium");

		assertFalse("Character's base size should not be < to Medium",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZEGT:Medium");

		assertFalse("Character's base size should not be > to Medium",
			PrereqHandler.passes(prereq, character, null));
	}

	/**
	 * Test to make sure template SIZE: changes the base size.
	 * @throws Exception
	 */
	public void testModBaseSize() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setRace(race);
		character.addTemplate(template);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREBASESIZEEQ:L");

		assertTrue("Character's base size should be equal to Large",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZELTEQ:L");

		assertTrue("Character's base size should be <= to L", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZEGTEQ:L");

		assertTrue("Character's base size should be >= to L", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZEGT:Small");

		assertTrue("Character's base size should be > to Small", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZELT:Huge");

		assertTrue("Character's base size should be < to Large", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZELT:L");

		assertFalse("Character's base size should not be < to L", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZEGT:L");

		assertFalse("Character's base size should not be > to Medium",
			PrereqHandler.passes(prereq, character, null));
	}

	/**
	 * Tests to make sure the base size does not includ SIZEMOD adjustments
	 * @throws Exception
	 */
	public void testBaseSizePlusMod() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setRace(race);
		final BonusObj sizeBonus = Bonus.newBonus("1|SIZEMOD|NUMBER|1");
		race.addBonusList(sizeBonus);

		character.calcActiveBonuses();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREBASESIZEEQ:Medium");

		assertTrue("Character's base size should be equal to Medium",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZELTEQ:Medium");

		assertTrue("Character's base size should be <= to Medium",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZEGTEQ:Medium");

		assertTrue("Character's base size should be >= to Medium",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZEGT:Small");

		assertTrue("Character's base size should be > to Small", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZELT:Large");

		assertTrue("Character's base size should be < to Large", PrereqHandler
			.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZELT:Medium");

		assertFalse("Character's base size should not be < to Medium",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREBASESIZEGT:Medium");

		assertFalse("Character's base size should not be > to Medium",
			PrereqHandler.passes(prereq, character, null));
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		race.setName("Human");
		race.setSize("M");
		Globals.addRace(race);

		template.setTemplateSize("L");
	}
}
