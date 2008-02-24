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

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.cdom.mode.Size;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.equipment.SizeToken;

public class SizeIntegrationTest extends
		AbstractIntegrationTestCase<CDOMEquipment>
{

	static SizeToken token = new SizeToken();
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

	@Override
	public CDOMPrimaryToken<CDOMEquipment> getToken()
	{
		return token;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		CDOMSizeAdjustment ps = primaryContext.ref.constructCDOMObject(
				CDOMSizeAdjustment.class, "S");
		primaryContext.ref.registerAbbreviation(ps, "S");
		ps.setName("Small");
		CDOMSizeAdjustment pm = primaryContext.ref.constructCDOMObject(
				CDOMSizeAdjustment.class, "M");
		secondaryContext.ref.registerAbbreviation(pm, "M");
		pm.setName("Medium");
		CDOMSizeAdjustment ss = secondaryContext.ref.constructCDOMObject(
				CDOMSizeAdjustment.class, "S");
		secondaryContext.ref.registerAbbreviation(ss, "S");
		ss.setName("Small");
		CDOMSizeAdjustment sm = secondaryContext.ref.constructCDOMObject(
				CDOMSizeAdjustment.class, "M");
		secondaryContext.ref.registerAbbreviation(sm, "M");
		sm.setName("Medium");
	}

	@Override
	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
		Size.clearConstants();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "M");
		commit(modCampaign, tc, "S");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "S");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "M");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
