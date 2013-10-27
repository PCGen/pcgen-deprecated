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
package plugin.lsttokens.pcclass.level;

import org.junit.Test;

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMPCClassLevel;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;

public class KnownspellsTokenTest extends
		AbstractListTokenTestCase<CDOMPCClassLevel, CDOMSpell>
{

	static KnownspellsToken token = new KnownspellsToken();
	static CDOMTokenLoader<CDOMPCClassLevel> loader = new CDOMTokenLoader<CDOMPCClassLevel>(
			CDOMPCClassLevel.class);

	private final CDOMPCClass primClass = new CDOMPCClass();
	private final CDOMPCClass secClass = new CDOMPCClass();
	
	@Override
	protected CDOMPCClassLevel getPrimary(String name)
	{
		return primClass.getClassLevel(1);
	}

	@Override
	protected CDOMPCClassLevel getSecondary(String name)
	{
		return secClass.getClassLevel(1);
	}

	@Override
	public Class<CDOMPCClassLevel> getCDOMClass()
	{
		return CDOMPCClassLevel.class;
	}

	@Override
	public CDOMLoader<CDOMPCClassLevel> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMPCClassLevel> getToken()
	{
		return token;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<CDOMSpell> getTargetClass()
	{
		return CDOMSpell.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Override
	@Test
	public void testInvalidInputJoinedComma() throws PersistenceLayerException
	{
		if (getJoinCharacter() != ',')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertFalse(parse("TestWP1,TestWP2"));
		}
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoType() throws PersistenceLayerException
	{
		assertFalse(parse("TYPE=TestWP1,TYPE=TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSpellAndType() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1,TYPE=TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLevelEmpty() throws PersistenceLayerException
	{
		assertFalse(parse("LEVEL="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLevelNaN() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse("LEVEL=One"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputLevelDouble() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse("LEVEL=1.0"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputStart() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse(",LEVEL=2"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputEnd() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse("LEVEL=2,"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputDouble() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse("TYPE=Foo,,LEVEL=2"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTwoLevel() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse("LEVEL=1,LEVEL=2"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testRoundRobinWithLevel() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1" + getJoinCharacter() + "LEVEL=1");
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
	}

	@Test
	public void testRoundRobinTestEqualThreeLevel()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin("LEVEL=2|TYPE=TestAltType.TestThirdType.TestType,LEVEL=3");
		}
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return true;
	}

	@Override
	public String getClearString()
	{
		return ".CLEARALL";
	}

}
