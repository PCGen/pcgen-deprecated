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

import org.junit.Test;

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.spell.CostToken;

public class CostIntegrationTest extends AbstractIntegrationTestCase<CDOMSpell>
{
	static CostToken token = new CostToken();
	static CDOMTokenLoader<CDOMSpell> loader = new CDOMTokenLoader<CDOMSpell>(
			CDOMSpell.class);

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
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Cleric");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "4");
		commit(modCampaign, tc, "10|Cleric,20");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemovePre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "15|Wizard,10");
		commit(modCampaign, tc, "5");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinModPre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "15|Wizard,20");
		commit(modCampaign, tc, "10|Wizard,4");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Sorcerer");
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Bard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Bard");
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "15|Bard,3|Sorcerer,25");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Bard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Bard");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "2|Bard,4");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
