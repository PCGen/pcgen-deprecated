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
package plugin.lsttokens.deity;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreLevelWriter;

public class DomainsTokenTest extends AbstractListTokenTestCase<CDOMDeity, CDOMDomain>
{
	static DomainsToken token = new DomainsToken();
	static CDOMTokenLoader<CDOMDeity> loader = new CDOMTokenLoader<CDOMDeity>(
			CDOMDeity.class);

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(new PreLevelParser());
		TokenRegistration.register(new PreClassParser());
		TokenRegistration.register(new PreLevelWriter());
		TokenRegistration.register(new PreClassWriter());
	}

	@Override
	public char getJoinCharacter()
	{
		return ',';
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
		return true;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return true;
	}

	@Override
	public boolean isClearLegal()
	{
		return true;
	}

	@Override
	public Class<CDOMDeity> getCDOMClass()
	{
		return CDOMDeity.class;
	}

	@Override
	public CDOMLoader<CDOMDeity> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMDeity> getToken()
	{
		return token;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(CDOMDomain.class, one);
	}

	@Test
	public void testRoundRobinAll() throws PersistenceLayerException
	{
		runRoundRobin("ALL");
	}

	@Test
	public void testInvalidOnlyPre() throws PersistenceLayerException
	{
		assertFalse(parse("!PRELEVEL:3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmbeddedNotPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1|!PRELEVEL:3|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1,TestWP2|PREFOO:3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNotBadPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1,TestWP2|!PREFOO:3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmbeddedPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1|PRELEVEL:4|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1,TestWP2|PRELEVEL:MIN=5");
	}

	@Test
	public void testRoundRobinNotPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1,TestWP2|!PRELEVEL:MIN=5");
	}

	@Test
	public void testRoundRobinDoublePre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1,TestWP2|PRECLASS:1,Fighter=1|PRELEVEL:MIN=5");
	}

	@Test
	public void testRoundRobinDupeTwoPrereqs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1|PRECLASS:1,Fighter=1",
			"TestWP1|PRECLASS:1,Wizard=1");
	}

	@Test
	public void testRoundRobinTwoPrereqs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1|PRECLASS:1,Fighter=1",
			"TestWP2|PRECLASS:1,Wizard=1");
	}
}
