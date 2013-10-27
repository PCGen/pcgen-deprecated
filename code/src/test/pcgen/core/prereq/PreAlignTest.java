/*
 * PreAlignTest.java
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
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreAlignTest</code> tests that the PREALIGN tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreAlignTest extends AbstractCharacterTestCase
{
	private Deity deity;

	public static void main(final String[] args)
	{
		TestRunner.run(PreAlignTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreAlignTest.class);
	}

	/**
	 * Test that numeric alignment values work correctly in Align tests.
	 * @throws Exception
	 */
	public void testNumeric() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setAlignment(3, false, true);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREALIGN:3");

		assertTrue("Number 3 should match character's alignment of 3",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREALIGN:6");

		assertFalse("Number 6 should not match character's alignment of 3",
			PrereqHandler.passes(prereq, character, null));
	}

	/**
	 * Test that alignment abbreviation values work correctly in Align tests.
	 * @throws Exception
	 */
	public void testAbbrev() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setAlignment(3, false, true);

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("align");
		prereq.setKey("NG");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertTrue("Abbrev NG should match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		prereq = new Prerequisite();
		prereq.setKind("align");
		prereq.setKey("LG");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse("Abbrev LG should not match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREALIGN:NG");
		assertTrue("Abbrev NG should match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
		prereq = factory.parse("PREALIGN:LG");
		assertFalse("Abbrev LG should not match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
	}

	/**
	 * Tests that this only passes if the character's alignment matches his
	 * diety's alignment.
	 * @throws Exception
	 */
	public void testDeity() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setAlignment(3, false, true);
		character.setDeity(deity);
		assertEquals("Deity should have been set for character.", deity,
			character.getDeity());

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PREALIGN:Deity");

		assertTrue("Number 3 should match deity's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		character.setAlignment(6, false, true);

		assertFalse("Number 6 should not match deity's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
	}

	public void testMulti() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		character.setAlignment(3, false, true);

		final PreParserFactory factory = PreParserFactory.getInstance();
		Prerequisite prereq = factory.parse("PREALIGN:2,3,5");

		assertTrue("2, 3, or 5 should match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREALIGN:2,5,8");
		assertFalse("2, 5, or 8 should not match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREALIGN:LE,NG,NE");
		assertTrue("LE, NG, or NE should match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));

		prereq = factory.parse("PREALIGN:LE,NE,CE");
		assertFalse(
			"LE, NE, or CE should not match character's alignment of NG",
			PrereqHandler.passes(prereq, character, null));
	}

	protected void setUp() throws Exception
	{
		deity = new Deity();
		deity.setName("TestDeity");
		deity.setAlignment("NG");
		deity.setFollowerAlignments("012345678");

		super.setUp();
	}
}
