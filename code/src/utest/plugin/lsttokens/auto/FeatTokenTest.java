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

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;

public class FeatTokenTest extends AbstractAutoTokenTestCase
{

	private static final FeatToken FEAT_TOKEN = new FeatToken();

	@Override
	protected FeatToken getSubToken()
	{
		return FEAT_TOKEN;
	}

	@Override
	protected Class<CDOMAbility> getSubTokenType()
	{
		return CDOMAbility.class;
	}

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		CDOMAbility obj = loadContext.ref.constructCDOMObject(CDOMAbility.class, one);
		loadContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, obj);
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	protected boolean isAllLegal()
	{
		return false;
	}

	@Override
	protected boolean isTypeLegal()
	{
		return false;
	}

	@Override
	protected boolean isPrereqLegal()
	{
		return false;
	}

	@Override
	protected boolean isListLegal()
	{
		return false;
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenString() + "|TestWP1|TestWP1");
	}

	// @Test
	// public void testRoundRobinDupeOnePrereq() throws
	// PersistenceLayerException
	// {
	// construct(primaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP1");
	// runRoundRobin(getSubTokenString() + "|TestWP1|TestWP1[PRERACE:1,Human]");
	// assertTrue(primaryContext.ref.validate());
	// assertTrue(secondaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testRoundRobinDupeDiffPrereqs()
	// throws PersistenceLayerException
	// {
	// System.err.println("=");
	// construct(primaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP1");
	// runRoundRobin(getSubTokenString() + "|TestWP1[PRERACE:1,Human]",
	// getSubTokenString() + "|TestWP1");
	// assertTrue(primaryContext.ref.validate());
	// assertTrue(secondaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testRoundRobinDupeTwoDiffPrereqs()
	// throws PersistenceLayerException
	// {
	// construct(primaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP1");
	// construct(primaryContext, "TestWP2");
	// construct(secondaryContext, "TestWP2");
	// runRoundRobin(
	// getSubTokenString() + "|TestWP1|TestWP1[PRERACE:1,Human]",
	// getSubTokenString() + "|TestWP2|TestWP2[PRERACE:1,Elf]");
	// assertTrue(primaryContext.ref.validate());
	// assertTrue(secondaryContext.ref.validate());
	// }

	@Override
	protected boolean isTypeDotLegal()
	{
		return true;
	}
}
