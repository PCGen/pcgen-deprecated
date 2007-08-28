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

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.DomainLoader;
import plugin.lsttokens.domain.FeatToken;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;

public class FeatIntegrationTest extends
		AbstractListIntegrationTestCase<Domain, WeaponProf>
{

	static FeatToken token = new FeatToken();
	static DomainLoader loader = new DomainLoader();

	@Override
	public Class<Domain> getCDOMClass()
	{
		return Domain.class;
	}

	@Override
	public LstObjectFileLoader<Domain> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Domain> getToken()
	{
		return token;
	}

	@Override
	public Class<WeaponProf> getTargetClass()
	{
		return WeaponProf.class;
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
		Ability obj = loadContext.ref.constructCDOMObject(Ability.class, one);
		loadContext.ref.reassociateReference(AbilityCategory.FEAT, obj);
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}
}
