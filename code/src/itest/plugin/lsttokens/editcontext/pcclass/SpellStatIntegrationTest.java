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
package plugin.lsttokens.editcontext.pcclass;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.pcclass.SpellstatToken;

public class SpellStatIntegrationTest extends
		AbstractIntegrationTestCase<CDOMPCClass>
{

	static SpellstatToken token = new SpellstatToken();
	static CDOMTokenLoader<CDOMPCClass> loader = new CDOMTokenLoader<CDOMPCClass>(
			CDOMPCClass.class);

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
		primaryContext.ref.constructCDOMObject(CDOMStat.class, "STR");
		secondaryContext.ref.constructCDOMObject(CDOMStat.class, "STR");
		primaryContext.ref.constructCDOMObject(CDOMStat.class, "INT");
		secondaryContext.ref.constructCDOMObject(CDOMStat.class, "INT");
	}

	@Override
	public Class<CDOMPCClass> getCDOMClass()
	{
		return CDOMPCClass.class;
	}

	@Override
	public CDOMLoader<CDOMPCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMPCClass> getToken()
	{
		return token;
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "STR");
		commit(modCampaign, tc, "INT");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "INT");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "STR");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinOneSpell() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "STR");
		commit(modCampaign, tc, "SPELL");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinSpellNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "SPELL");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinSpellNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "SPELL");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}