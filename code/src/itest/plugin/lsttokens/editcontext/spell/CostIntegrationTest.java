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

import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SpellLoader;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.spell.CostToken;

public class CostIntegrationTest extends AbstractIntegrationTestCase<Spell>
{
	static CostToken token = new CostToken();
	static SpellLoader loader = new SpellLoader();

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
		primaryContext.ref.constructCDOMObject(PCClass.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Cleric");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "4");
		commit(modCampaign, tc, "10|Cleric,20");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemovePre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "15|Wizard,10");
		commit(modCampaign, tc, "5");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinModPre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "15|Wizard,20");
		commit(modCampaign, tc, "10|Wizard,4");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Sorcerer");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Bard");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Bard");
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "15|Bard,3|Sorcerer,25");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Bard");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Bard");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "2|Bard,4");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
