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

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMPCClassLevel;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

public class DomainTokenTest extends AbstractListTokenTestCase<CDOMPCClassLevel, CDOMDomain>
{

	static DomainToken token = new DomainToken();
	static CDOMTokenLoader<CDOMPCClassLevel> loader = new CDOMTokenLoader<CDOMPCClassLevel>(
			CDOMPCClassLevel.class);

	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
	}

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
	public Class<CDOMDomain> getTargetClass()
	{
		return CDOMDomain.class;
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

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void testInvalidEmptyPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1[]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyPre2() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1["));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyPre3() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMismatchedBracket() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1[PRERACE:Dwarf"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTrailingAfterBracket()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1[PRERACE:Dwarf]Hi"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinOnePre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1[PRERACE:1,Dwarf]");
	}

	@Test
	public void testRoundRobinThreeWithPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin("TestWP1[PRERACE:1,Dwarf]" + getJoinCharacter()
			+ "TestWP2[PRERACE:1,Human]" + getJoinCharacter() + "TestWP3");
	}

}
