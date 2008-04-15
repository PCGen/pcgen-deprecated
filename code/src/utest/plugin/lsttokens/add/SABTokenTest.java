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
package plugin.lsttokens.add;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public class SABTokenTest extends AbstractGlobalTokenTestCase
{
	private static AddLst token = new AddLst();

	private static AddLstToken aToken = new SABToken();

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(aToken);
	}

	protected char getJoinCharacter()
	{
		return ',';
	}

	public String getSubTokenString()
	{
		return getSubToken().getTokenName();
	}

	protected AddLstToken getSubToken()
	{
		return aToken;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNameOnly() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Name|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyName() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "||Sab"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|TestWP1|TestWP2|TestWP3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Name|TestWP1,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Name|,TestWP1"));
		assertNoSideEffects();
	}

	/*
	 * TODO should we catch this - this does NOT fail in 5.x
	 */
	// @Test
	// public void testInvalidOnlyCount() throws PersistenceLayerException
	// {
	// try
	// {
	// if (parse(getSubTokenString() + "|Name|1"))
	// {
	// assertFalse(primaryContext.ref.validate());
	// }
	// else
	// {
	// assertNoSideEffects();
	// }
	// }
	// catch (IllegalArgumentException e)
	// {
	// // OK as well
	// assertNoSideEffects();
	// }
	// }
	@Test
	public void testInvalidZeroCount() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Name|0|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeCount() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Name|-4|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Name|TestWP2,,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenString() + "|Name|TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString() + "|Name|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString() + "|Name|2|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|Name|TestWP1");
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|Name|TestWP1,TestWP2,TestWP3");
	}

	@Test
	public void testRoundRobinDouble() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|Name|TestWP1,TestWP2",
				getSubTokenString() + "|Name|TestWP1,TestWP3");
	}

	/*
	 * TODO Need to be able to do this
	 */
	// @Test
	// public void testRoundRobinDupe() throws PersistenceLayerException
	// {
	// runRoundRobin(getSubTokenString() + "|Name|TestWP1,TestWP2,TestWP3",
	// getSubTokenString() + "|Name|TestWP1,TestWP2,TestWP3");
	// }

	@Test
	public void testRoundRobinTwoCountThree() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|Name|2|TestWP1,TestWP2,TestWP3");
	}

	@Test
	public void testInputInvalidAddsBasicNoSideEffect()
			throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenString() + "|Name|TestWP1"
				+ getJoinCharacter() + "TestWP2"));
		assertTrue(parseSecondary(getSubTokenString() + "|Name|TestWP1"
				+ getJoinCharacter() + "TestWP2"));
		assertFalse(parse(getSubTokenString() + "|Name|TestWP3"
				+ getJoinCharacter() + getJoinCharacter() + "TestWP4"));
		assertNoSideEffects();
	}
}
