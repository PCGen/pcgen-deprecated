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

import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.list.ClassSkillList;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;

public class ClassesTokenTest extends AbstractTokenTestCase<CDOMSkill>
{

	static ClassesToken token = new ClassesToken();
	static CDOMTokenLoader<CDOMSkill> loader = new CDOMTokenLoader<CDOMSkill>(
			CDOMSkill.class);

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
	public Class<CDOMSkill> getCDOMClass()
	{
		return CDOMSkill.class;
	}

	@Override
	public CDOMLoader<CDOMSkill> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMSkill> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLeadingBar() throws PersistenceLayerException
	{
		assertFalse(parse("|Wizard"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingBar() throws PersistenceLayerException
	{
		assertFalse(parse("Wizard|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegationMix() throws PersistenceLayerException
	{
		assertFalse(parse("Wizard|!Sorcerer"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegationMixTwo()
		throws PersistenceLayerException
	{
		assertFalse(parse("!Wizard|Sorcerer"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("Wizard||Sorcerer"));
		assertNoSideEffects();
	}

	// @Test
	// public void testInvalidInputEmptyType() throws PersistenceLayerException
	// {
	// assertFalse(parse( "TYPE."));
	// }

	@Test
	public void testInvalidInputNotClass() throws PersistenceLayerException
	{
		assertTrue(parse("Wizard"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputNotClassCompound()
		throws PersistenceLayerException
	{
		assertTrue(parse("Wizard|Sorcerer"));
		assertFalse(primaryContext.ref.validate());
	}

	// @Test(expected = IllegalArgumentException.class)
	public void testInvalidInputAllPlus() throws PersistenceLayerException
	{
		try
		{
			assertFalse(parse("Wizard|ALL"));
		}
		catch (IllegalArgumentException iae)
		{
			// OK as well
		}
		assertNoSideEffects();
	}

	// @Test(expected = IllegalArgumentException.class)
	public void testInvalidInputNegativeAllPlus()
		throws PersistenceLayerException
	{
		try
		{
			assertFalse(parse("!Wizard|ALL"));
		}
		catch (IllegalArgumentException iae)
		{
			// OK as well
		}
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeAll() throws PersistenceLayerException
	{
		// This technically gets caught by the PRECLASS parser...
		assertFalse(parse("!ALL"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinAll() throws PersistenceLayerException
	{
		runRoundRobin("ALL");
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(ClassSkillList.class, "Wizard");
		secondaryContext.ref
			.constructCDOMObject(ClassSkillList.class, "Wizard");
		runRoundRobin("Wizard");
	}

	@Test
	public void testRoundRobinNegated() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(ClassSkillList.class, "Wizard");
		secondaryContext.ref
			.constructCDOMObject(ClassSkillList.class, "Wizard");
		runRoundRobin("!Wizard");
	}

	@Test
	public void testRoundRobinPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(ClassSkillList.class, "Wizard");
		secondaryContext.ref
			.constructCDOMObject(ClassSkillList.class, "Wizard");
		primaryContext.ref
			.constructCDOMObject(ClassSkillList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSkillList.class,
			"Sorcerer");
		runRoundRobin("Sorcerer|Wizard");
	}

	@Test
	public void testRoundRobinNegatedPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(ClassSkillList.class, "Wizard");
		secondaryContext.ref
			.constructCDOMObject(ClassSkillList.class, "Wizard");
		primaryContext.ref
			.constructCDOMObject(ClassSkillList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSkillList.class,
			"Sorcerer");
		runRoundRobin("!Sorcerer|!Wizard");
	}
}
