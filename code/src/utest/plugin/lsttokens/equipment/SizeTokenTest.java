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
package plugin.lsttokens.equipment;

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
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class SizeTokenTest extends AbstractTokenTestCase<CDOMEquipment>
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
				CDOMSizeAdjustment.class, "Small");
		primaryContext.ref.registerAbbreviation(ps, "S");
		CDOMSizeAdjustment pm = primaryContext.ref.constructCDOMObject(
				CDOMSizeAdjustment.class, "Medium");
		primaryContext.ref.registerAbbreviation(pm, "M");
		CDOMSizeAdjustment ss = secondaryContext.ref.constructCDOMObject(
				CDOMSizeAdjustment.class, "Small");
		secondaryContext.ref.registerAbbreviation(ss, "S");
		CDOMSizeAdjustment sm = secondaryContext.ref.constructCDOMObject(
				CDOMSizeAdjustment.class, "Medium");
		secondaryContext.ref.registerAbbreviation(sm, "M");
	}

	@Override
	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
		Size.clearConstants();
	}

	@Test
	public void testInvalidNotASize()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "W"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinS() throws PersistenceLayerException
	{
		runRoundRobin("S");
	}

	@Test
	public void testRoundRobinM() throws PersistenceLayerException
	{
		runRoundRobin("M");
	}

}
