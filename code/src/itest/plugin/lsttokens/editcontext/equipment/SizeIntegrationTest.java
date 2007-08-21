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

import pcgen.cdom.mode.Size;
import pcgen.core.Equipment;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.equipment.SizeToken;

public class SizeIntegrationTest extends AbstractIntegrationTestCase<Equipment>
{

	static SizeToken token = new SizeToken();
	static EquipmentLoader loader = new EquipmentLoader();

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public LstObjectFileLoader<Equipment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Equipment> getToken()
	{
		return token;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		SizeAdjustment ps = primaryContext.ref.constructCDOMObject(SizeAdjustment.class, "S");
		ps.setAbbreviation("S");
		SizeAdjustment pm = primaryContext.ref.constructCDOMObject(SizeAdjustment.class, "M");
		pm.setAbbreviation("M");
		SizeAdjustment ss = secondaryContext.ref.constructCDOMObject(SizeAdjustment.class, "S");
		ss.setAbbreviation("S");
		SizeAdjustment sm = secondaryContext.ref.constructCDOMObject(SizeAdjustment.class, "M");
		sm.setAbbreviation("M");
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
