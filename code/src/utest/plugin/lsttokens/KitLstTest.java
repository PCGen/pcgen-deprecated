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
import pcgen.cdom.inst.CDOMKit;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class KitLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new KitLst();
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
	public void testInvalidInputNoNumber() throws PersistenceLayerException
	{
		assertFalse(parse("TestType"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMissingNumber()
			throws PersistenceLayerException
	{
		assertFalse(parse("|TestType"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMissingItem() throws PersistenceLayerException
	{
		assertFalse(parse("2|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("2||TestWP1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinedComma() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		if (parse("1|TestWP1,TestWP2"))
		{
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		if (parse("1|TestWP1.TestWP2"))
		{
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("1|TestWP1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("1||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNaNCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("Count|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidZeroCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("0|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("-4|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("1|TestWP2||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse("1|TestWP1|TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("1|TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse("1|TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("1|TestWP1");
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin("1|TestWP1|TestWP2|TestWP3");
	}

	@Test
	public void testRoundRobinTwoCountThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin("2|TestWP1|TestWP2|TestWP3");
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(CDOMKit.class, one);
	}

}
