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
package plugin.lsttokens.editcontext.domain;

import org.junit.Test;

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.domain.FeatToken;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;

public class FeatIntegrationTest extends
		AbstractListIntegrationTestCase<CDOMDomain, CDOMWeaponProf>
{

	static FeatToken token = new FeatToken();
	static CDOMTokenLoader<CDOMDomain> loader = new CDOMTokenLoader<CDOMDomain>(
			CDOMDomain.class);

	@Override
	public Class<CDOMDomain> getCDOMClass()
	{
		return CDOMDomain.class;
	}

	@Override
	public CDOMLoader<CDOMDomain> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMDomain> getToken()
	{
		return token;
	}

	@Override
	public Class<CDOMWeaponProf> getTargetClass()
	{
		return CDOMWeaponProf.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
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
		return true;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		CDOMAbility obj = loadContext.ref.constructCDOMObject(
				CDOMAbility.class, one);
		loadContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, obj);
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}
}
