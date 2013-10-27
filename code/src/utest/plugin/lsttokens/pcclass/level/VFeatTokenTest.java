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

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMPCClassLevel;
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

public class VFeatTokenTest extends AbstractListTokenTestCase<CDOMPCClassLevel, CDOMAbility>
{
	static VfeatToken token = new VfeatToken();
	static CDOMTokenLoader<CDOMPCClassLevel> loader = new CDOMTokenLoader<CDOMPCClassLevel>(
			CDOMPCClassLevel.class);

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
	protected void construct(LoadContext loadContext, String one)
	{
		CDOMAbility obj = loadContext.ref.constructCDOMObject(CDOMAbility.class, one);
		loadContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, obj);
	}

	@Test
	public void testInvalidInputEmpty()
	{
		assertFalse(token.parse(primaryContext, primaryProf, ""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyPre()
	{
		construct(primaryContext, "TestWP1");
		try
		{
			assertFalse(token.parse(primaryContext, primaryProf,
				"PRECLASS:1,Fighter=1"));
		}
		catch (IllegalArgumentException e)
		{
			// this is okay too :)
		}
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmbeddedPre()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf,
			"TestWP1|PRECLASS:1,Fighter=1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipePre()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf,
			"TestWP1||PRECLASS:1,Fighter=1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputPostPrePipe()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf,
			"TestWP1|PRECLASS:1,Fighter=1|"));
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
		System.err.println("=");
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
