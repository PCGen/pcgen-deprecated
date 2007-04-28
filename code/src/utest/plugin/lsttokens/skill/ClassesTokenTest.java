/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.skill;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SkillLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;

public class ClassesTokenTest extends AbstractTokenTestCase<Skill>
{

	static ClassesToken token = new ClassesToken();
	static SkillLoader loader = new SkillLoader();

	private static boolean classSetUpFired = false;

	@BeforeClass
	public static final void ltClassSetUp() throws PersistenceLayerException
	{
		TokenRegistration.register(new PreClassParser());
		classSetUpFired = true;
	}

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		if (!classSetUpFired)
		{
			ltClassSetUp();
		}
	}

	@Override
	public Class<Skill> getCDOMClass()
	{
		return Skill.class;
	}

	@Override
	public LstObjectFileLoader<Skill> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Skill> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputLeadingBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|Wizard"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNegationMix() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Wizard|!Sorcerer"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNegationMixTwo()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"!Wizard|Sorcerer"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Wizard||Sorcerer"));
		assertTrue(primaryGraph.isEmpty());
	}

	// @Test
	// public void testInvalidInputEmptyType() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "TYPE."));
	// }

	@Test
	public void testInvalidInputNotClass() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "Wizard"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputNotClassCompound()
		throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Wizard|Sorcerer"));
		assertFalse(primaryContext.ref.validate());
	}

	// @Test(expected = IllegalArgumentException.class)
	public void testInvalidInputAllPlus() throws PersistenceLayerException
	{
		try
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"Wizard|ALL"));
		}
		catch (IllegalArgumentException iae)
		{
			// OK as well
		}
	}

	// @Test(expected = IllegalArgumentException.class)
	public void testInvalidInputNegativeAllPlus()
		throws PersistenceLayerException
	{
		try
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"!Wizard|ALL"));
		}
		catch (IllegalArgumentException iae)
		{
			// OK as well
		}
	}

	@Test
	public void testInvalidInputNegativeAll() throws PersistenceLayerException
	{
		// This technically gets caught by the PRECLASS parser...
		assertFalse(getToken().parse(primaryContext, primaryProf, "!ALL"));
	}

	@Test
	public void testRoundRobinAll() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		runRoundRobin("ALL");
		assertTrue(primaryContext.ref.validate());
		assertEquals(0, primaryContext.getWriteMessageCount());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		runRoundRobin("Wizard");
	}

	@Test
	public void testRoundRobinNegated() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		runRoundRobin("!Wizard");
	}

	@Test
	public void testRoundRobinPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		runRoundRobin("Sorcerer|Wizard");
	}

	@Test
	public void testRoundRobinNegatedPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		runRoundRobin("!Sorcerer|!Wizard");
	}
}
