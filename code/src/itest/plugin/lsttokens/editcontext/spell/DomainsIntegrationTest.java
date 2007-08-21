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

import pcgen.core.DomainSpellList;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SpellLoader;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.spell.DomainsToken;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

public class DomainsIntegrationTest extends AbstractIntegrationTestCase<Spell>
{

	static DomainsToken token = new DomainsToken();
	static SpellLoader loader = new SpellLoader();

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
	public Class<Spell> getCDOMClass()
	{
		return Spell.class;
	}

	@Override
	public LstObjectFileLoader<Spell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Spell> getToken()
	{
		return token;
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Good");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Good");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Law");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Law");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Fire=4");
		commit(modCampaign, tc, "Good=3|Law=4");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemovePre() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Fire=4[PRERACE:1,Human]");
		commit(modCampaign, tc, "Fire=4");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAddPre() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Fire=4");
		commit(modCampaign, tc, "Fire=4[PRERACE:1,Human]");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Good");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Good");
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Good=3");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Good");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Good");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Fire,Good=2");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
