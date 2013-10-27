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
package plugin.lsttokens;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalListTokenTestCase;

public class TemplateLstTest extends
		AbstractGlobalListTokenTestCase<CDOMTemplate>
{

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<CDOMTemplate> getTargetClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

	static CDOMPrimaryToken<CDOMObject> token = new TemplateLst();
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testChooseInvalidInputString() throws PersistenceLayerException
	{
		assertTrue(parse("CHOOSE:String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidInputType() throws PersistenceLayerException
	{
		assertTrue(parse("CHOOSE:TestType"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidInputJoinedComma()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("CHOOSE:TestWP1,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidInputJoinedDot()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("CHOOSE:TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("CHOOSE:TestWP1" + getJoinCharacter()));
		assertNoSideEffects();
	}

	@Test
	public void testChooseInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("CHOOSE:" + getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testChooseInvalidListDoubleJoin()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("CHOOSE:TestWP2" + getJoinCharacter()
				+ getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testChooseInvalidInputCheckMult()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build testChooseWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse("CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse("CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testChooseRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("CHOOSE:TestWP1");
	}

	@Test
	public void testChooseRoundRobinThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin("CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"
				+ getJoinCharacter() + "TestWP3");
	}

	@Test
	public void testChooseInvalidInputAddString()
			throws PersistenceLayerException
	{
		assertTrue(parse("ADDCHOICE:String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidInputAddType()
			throws PersistenceLayerException
	{
		assertTrue(parse("ADDCHOICE:TestType"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidInputAddJoinedComma()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("ADDCHOICE:TestWP1,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidInputAddJoinedDot()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("ADDCHOICE:TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testChooseInvalidAddListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("ADDCHOICE:TestWP1" + getJoinCharacter()));
		assertNoSideEffects();
	}

	@Test
	public void testChooseInvalidAddListStart()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("ADDCHOICE:" + getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testChooseInvalidAddListDoubleJoin()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("ADDCHOICE:TestWP2" + getJoinCharacter()
				+ getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testChooseInvalidAddInputCheckMult()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build testChooseWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse("ADDCHOICE:TestWP1" + getJoinCharacter() + "TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	/*
	 * TODO Need to do tests with ADDCHOICE:
	 */
	// @Test
	// public void testChooseRoundRobinAddOne() throws PersistenceLayerException
	// {
	// construct(primaryContext, "TestWP1");
	// construct(primaryContext, "TestWP2");
	// construct(secondaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP2");
	// runRoundRobin("ADDCHOICE:TestWP1");
	// }
	//
	// @Test
	// public void testChooseRoundRobinAddThree() throws
	// PersistenceLayerException
	// {
	// construct(primaryContext, "TestWP1");
	// construct(primaryContext, "TestWP2");
	// construct(primaryContext, "TestWP3");
	// construct(secondaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP2");
	// construct(secondaryContext, "TestWP3");
	// runRoundRobin("ADDCHOICE:TestWP1" + getJoinCharacter() + "TestWP2"
	// + getJoinCharacter() + "TestWP3");
	// }
	//
}
