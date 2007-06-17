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

import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

public class DomainTokenTest extends AbstractListTokenTestCase<PObject, Domain>
{

	static DomainToken token = new DomainToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
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
	public Class<Domain> getTargetClass()
	{
		return Domain.class;
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
		return true;
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
		assertFalse(getToken().parse(primaryContext, primaryProf, "TestWP1[]"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptyPre2() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf, "TestWP1["));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptyPre3() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf, "TestWP1]"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidMismatchedBracket() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP1[PRERACE:Dwarf"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTrailingAfterBracket()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TestWP1[PRERACE:Dwarf]Hi"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinOnePre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1[PRERACE:1,Dwarf]");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
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
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

}
