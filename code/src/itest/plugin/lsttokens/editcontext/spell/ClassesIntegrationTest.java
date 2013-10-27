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
package plugin.lsttokens.editcontext.spell;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.list.ClassSpellList;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.spell.ClassesToken;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

public class ClassesIntegrationTest extends
		AbstractIntegrationTestCase<CDOMSpell>
{

	static ClassesToken token = new ClassesToken();
	static CDOMTokenLoader<CDOMSpell> loader = new CDOMTokenLoader<CDOMSpell>(
			CDOMSpell.class);

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

	@Override
	public Class<CDOMSpell> getCDOMClass()
	{
		return CDOMSpell.class;
	}

	@Override
	public CDOMLoader<CDOMSpell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMSpell> getToken()
	{
		return token;
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		primaryContext.ref
				.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class,
				"Sorcerer");
		secondaryContext.ref
				.constructCDOMObject(ClassSpellList.class, "Cleric");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Wizard=4");
		commit(modCampaign, tc, "Sorcerer=3|Cleric=4");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemovePre() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Wizard=4[PRERACE:1,Human]");
		commit(modCampaign, tc, "Wizard=4");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAddPre() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Wizard=4");
		commit(modCampaign, tc, "Wizard=4[PRERACE:1,Human]");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref
				.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class,
				"Sorcerer");
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Sorcerer=3");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		primaryContext.ref
				.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class,
				"Sorcerer");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Sorcerer,Wizard=2");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAll() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "ALL=2");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAllClear() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "ALL=2");
		commit(modCampaign, tc, ".CLEARALL");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAllSupplement() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref
				.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class,
				"Sorcerer");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "ALL=2");
		commit(modCampaign, tc, "Sorcerer=1");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinClearEmtpy() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, ".CLEARALL");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinEmptyClear() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, ".CLEARALL");
		completeRoundRobin(tc);
	}

}
