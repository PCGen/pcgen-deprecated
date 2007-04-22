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
package plugin.lsttokens.pcclass;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;

public class KnownspellsTokenTest extends
		AbstractListTokenTestCase<PObject, Spell>
{

	static KnownspellsToken token = new KnownspellsToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
	}

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public LstLoader getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PObject> getToken()
	{
		return token;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<Spell> getTargetClass()
	{
		return Spell.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
	}

	@Override
	@Test
	public void testInvalidInputJoinedComma() throws PersistenceLayerException
	{
		if (getJoinCharacter() != ',')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"TestWP1,TestWP2"));
		}
	}

	@Test
	public void testInvalidInputTwoType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE=TestWP1,TYPE=TestWP2"));
	}

	@Test
	public void testInvalidInputSpellAndType() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP1,TYPE=TestWP2"));
	}

	@Test
	public void testInvalidInputLevelEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "LEVEL="));
	}

	@Test
	public void testInvalidInputLevelNaN() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"LEVEL=One"));
		}
	}

	@Test
	public void testInvalidInputLevelDouble() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"LEVEL=1.0"));
		}
	}

	@Test
	public void testInvalidInputTwoLevel() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				"LEVEL=1,LEVEL=2"));
		}
	}

	@Test
	public void testRoundRobinWithLevel() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1" + getJoinCharacter() + "LEVEL=1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinTypeLevel() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin("TestWP1" + getJoinCharacter() + "TYPE=SpellType,LEVEL=1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinTestEqualThreeLevel()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin("LEVEL=2|TYPE=TestAltType.TestThirdType.TestType,LEVEL=3");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

}
