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
package plugin.lsttokens.editcontext.pcclass;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.SkillList;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import plugin.lsttokens.pcclass.SkilllistToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class SkillListTokenTest extends AbstractTokenTestCase<PCClass>
{
	static SkilllistToken token = new SkilllistToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
	}

	@Override
	public Class<? extends PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public LstLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
	}

	@Test
	public void testInvalidInputUnbuilt() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "1|String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputNoCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"|TestWP1|TestWP2"));
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"1|TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAll()
	// {
	// assertTrue(getToken().parse(primaryContext, primaryProf,
	// "|ALL"));
	// assertFalse(primaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testInvalidInputAny()
	// {
	// assertTrue(getToken().parse(primaryContext, primaryProf,
	// "|ANY"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// @Test
	// public void testInvalidInputCheckType()
	// {
	// if (!isTypeLegal())
	// {
	// assertTrue(token.parse(primaryContext, primaryProf,
	// "|TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// }
	//

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf, "1|TestWP1|"));
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf, "1||TestWP1"));
	}

	@Test
	public void testInvalidZeroCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf, "0|TestWP1"));
	}

	@Test
	public void testInvalidNegativeCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"-1|TestWP1|TestWP2"));
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"1|TestWP2||TestWP1"));
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"1|TestWP1|TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf, "1|TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"1|TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"2|TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("1|TestWP1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
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
		runRoundRobin("2|TestWP1|TestWP2|TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(SkillList.class, one);
	}

}
