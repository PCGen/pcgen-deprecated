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
package plugin.lsttokens.editcontext.equipment;

import org.junit.Test;

import pcgen.cdom.inst.CDOMEquipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;

public abstract class AbstractCritMultIntegrationTestCase extends
		AbstractIntegrationTestCase<CDOMEquipment>
{

	static CDOMTokenLoader<CDOMEquipment> loader = new CDOMTokenLoader<CDOMEquipment>(
			CDOMEquipment.class);

	@Override
	public Class<CDOMEquipment> getCDOMClass()
	{
		return CDOMEquipment.class;
	}

	@Override
	public CDOMLoader<CDOMEquipment> getLoader()
	{
		return loader;
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "x3");
		commit(modCampaign, tc, "x4");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinRemove() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "x3");
		commit(modCampaign, tc, "-");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAdd() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "-");
		commit(modCampaign, tc, "x2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "x2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "x5");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinSpecNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "-");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinSpecNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "-");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
