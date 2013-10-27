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
package plugin.lsttokens.template;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class FeatTokenTest extends
		AbstractListTokenTestCase<CDOMTemplate, CDOMAbility>
{
	static FeatToken token = new FeatToken();

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	PreClassParser preclass = new PreClassParser();

	PreClassWriter preclasswriter = new PreClassWriter();

	PreRaceParser prerace = new PreRaceParser();

	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<CDOMAbility> getTargetClass()
	{
		return CDOMAbility.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		/*
		 * TODO Need to change this to true!
		 */
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
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMTemplate> getToken()
	{
		return token;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		CDOMAbility obj = loadContext.ref.constructCDOMObject(CDOMAbility.class, one);
		loadContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, obj);
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		try
		{
			assertFalse(parse("PRECLASS:1,Fighter=1"));
		}
		catch (IllegalArgumentException e)
		{
			// this is okay too :)
		}
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmbeddedPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1|PRECLASS:1,Fighter=1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipePre()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1||PRECLASS:1,Fighter=1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputPostPrePipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1|PRECLASS:1,Fighter=1|"));
		assertNoSideEffects();
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

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1|TestWP1");
	}

	@Test
	public void testRoundRobinDupeOnePrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1|TestWP1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinDupeDiffPrereqs()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1", "TestWP1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinDupeTwoDiffPrereqs()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1|TestWP1|PRERACE:1,Human",
			"TestWP2|TestWP2|PRERACE:1,Elf");
	}
}
