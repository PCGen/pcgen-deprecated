/*
 * PreDeityTest.java
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
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PreDeityTest</code> tests that the PREDEITY tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreDeityTest extends AbstractCharacterTestCase
{
	private Deity deity;

	public static void main(final String[] args)
	{
		TestRunner.run(PreDeityTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreDeityTest.class);
	}

	/**
	 * Test that the boolean version (Y/N) works
	 * @throws Exception
	 */
	public void testBoolean() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:Y");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:N");

		assertTrue("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		character.setAlignment(3, false);
		character.setDeity(deity);

		assertFalse("Character has deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:Y");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

	}

	/**
	 * Test different formats for the option
	 * @throws Exception
	 */
	public void testFormat() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:YES");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:NO");

		assertTrue("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		character.setAlignment(3, false);
		character.setDeity(deity);

		assertFalse("Character has deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:YES");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDEITY:yes");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDEITY:Yesmeth");

		assertFalse("Character does not have Yesmeth as deity", PrereqHandler
			.passes(prereq, character, null));
	}

	/**
	 * Test naming specific deities works as expected
	 * @throws Exception
	 */
	public void testName() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:Test Deity");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		character.setAlignment(3, false);
		character.setDeity(deity);

		assertTrue("Character has Test Deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:Test Deity,Zeus,Odin");

		assertTrue("Character has Test Deity selected", PrereqHandler.passes(
			prereq, character, null));
	}

	/**
	 * Test that the new standardised format works correctly.
	 * @throws PersistenceLayerException
	 */
	public void testNewFormat() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,YES");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,NO");

		assertTrue("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		character.setAlignment(3, false);
		character.setDeity(deity);

		assertFalse("Character has deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:1,YES");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDEITY:1,yes");

		assertTrue("Character has deity selected", PrereqHandler.passes(prereq,
			character, null));

		prereq = factory.parse("PREDEITY:1,Yesmeth");

		assertFalse("Character does not have Yesmeth as deity", PrereqHandler
			.passes(prereq, character, null));
	}

	/**
	 * Test the pantheon fucntioanlity of the PREDEITY tag. 
	 * @throws PersistenceLayerException 
	 */
	public void testPantheon() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREDEITY:1,PANTHEON.Celtic");

		assertFalse("Character has no deity selected", PrereqHandler.passes(
			prereq, character, null));

		character.setAlignment(3, false);
		character.setDeity(deity);

		assertTrue("Character has Celtic deity selected", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREDEITY:Zeus,PANTHEON.Celtic,Odin");

		assertTrue("Character has Celtic deity selected", PrereqHandler.passes(
			prereq, character, null));
	}

	protected void setUp() throws Exception
	{
		deity = new Deity();
		deity.setName("Test Deity");
		deity.setAlignment("NG");
		deity.setFollowerAlignments("012345678");
		deity.addPantheon("Celtic");

		super.setUp();
	}
}
