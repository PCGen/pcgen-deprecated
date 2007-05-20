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

import org.junit.Test;

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class FeatAutoTokenTest extends
		AbstractListTokenTestCase<PObject, Ability>
{

	static FeatautoToken token = new FeatautoToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

	PreClassParser preclass = new PreClassParser();
	PreClassWriter preclasswriter = new PreClassWriter();
	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
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
	public Class<Ability> getTargetClass()
	{
		return Ability.class;
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
		return true;
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
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.ref.constructCDOMObject(Ability.class, one);
		loadContext.ref.reassociateReference(AbilityCategory.FEAT, obj);
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputOnlyPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		try
		{
			assertFalse(token.parse(primaryContext, primaryProf,
				"PRECLASS:1,Fighter=1"));
		}
		catch (IllegalArgumentException e)
		{
			// This is okay too :)
		}
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmbeddedPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf,
			"TestWP1|PRECLASS:1,Fighter=1|TestWP2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipePre()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf,
			"TestWP1||PRECLASS:1,Fighter=1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputPostPrePipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf,
			"TestWP1|PRECLASS:1,Fighter=1|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1|PRECLASS:1,Fighter=1");
	}

	@Test
	public void testRoundRobinTwoPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1|!PRERACE:1,Human|PRECLASS:1,Fighter=1");
	}

	@Test
	public void testRoundRobinNotPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1|!PRECLASS:1,Fighter=1");
	}

	@Test
	public void testRoundRobinWWoPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1|PRECLASS:1,Fighter=1", "TestWP2");
	}

}
