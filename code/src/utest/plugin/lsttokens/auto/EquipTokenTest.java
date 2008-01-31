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
package plugin.lsttokens.auto;

import org.junit.Test;

import pcgen.core.Equipment;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;

public class EquipTokenTest extends AbstractAutoTokenTestCase
{

	private static final EquipToken EQUIP_TOKEN = new EquipToken();

	@Override
	protected EquipToken getSubToken()
	{
		return EQUIP_TOKEN;
	}

	@Override
	protected Class<Equipment> getSubTokenType()
	{
		return Equipment.class;
	}

	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	protected boolean isAllLegal()
	{
		return false;
	}

	@Override
	protected boolean isTypeLegal()
	{
		return true;
	}

	@Override
	protected boolean isPrereqLegal()
	{
		return true;
	}

	@Override
	protected boolean isListLegal()
	{
		return true;
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenString() + "|TestWP1|TestWP1");
	}

	@Test
	public void testRoundRobinDupeOnePrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenString() + "|TestWP1|TestWP1[PRERACE:1,Human]");
	}

	@Test
	public void testRoundRobinDupeDiffPrereqs()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenString() + "|TestWP1[PRERACE:1,Human]",
			getSubTokenString() + "|TestWP1");
	}

	@Test
	public void testRoundRobinDupeTwoDiffPrereqs()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(
			getSubTokenString() + "|TestWP1|TestWP1[PRERACE:1,Human]",
			getSubTokenString() + "|TestWP2|TestWP2[PRERACE:1,Elf]");
	}

	@Override
	protected boolean isTypeDotLegal()
	{
		return true;
	}
}
