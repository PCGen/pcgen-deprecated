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

import pcgen.cdom.inst.CDOMEqMod;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;

public abstract class AbstractEqModIntegrationTestCase extends
		AbstractListIntegrationTestCase<CDOMEquipment, CDOMEqMod>
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

	@Override
	public Class<CDOMEqMod> getTargetClass()
	{
		return CDOMEqMod.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public char getJoinCharacter()
	{
		return '.';
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
	public boolean isPrereqLegal()
	{
		return false;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	@Test
	public void testRoundRobinMods() throws PersistenceLayerException
	{
		construct(primaryContext, "EQMOD2");
		construct(secondaryContext, "EQMOD2");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "EQMOD2|9500");
		commit(modCampaign, tc, "EQMOD2|COST[9500]");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinModsTwo() throws PersistenceLayerException
	{
		construct(primaryContext, "EQMOD2");
		construct(secondaryContext, "EQMOD2");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "EQMOD2|COST[9500]PLUS[+1]");
		commit(modCampaign, tc, "EQMOD2|COST[9500]");
		completeRoundRobin(tc);
	}

}
