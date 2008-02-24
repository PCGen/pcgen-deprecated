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
package plugin.lsttokens.deity;

import org.junit.Test;

import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;

public class DeityWeapTokenTest extends
		AbstractListTokenTestCase<CDOMDeity, CDOMWeaponProf>
{
	static DeityweapToken token = new DeityweapToken();
	static CDOMTokenLoader<CDOMDeity> loader = new CDOMTokenLoader<CDOMDeity>(
			CDOMDeity.class);

	@Override
	public char getJoinCharacter()
	{
		return '|';
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
	public boolean isAllLegal()
	{
		return false;
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
	public Class<CDOMDeity> getCDOMClass()
	{
		return CDOMDeity.class;
	}

	@Override
	public CDOMLoader<CDOMDeity> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMDeity> getToken()
	{
		return token;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(CDOMWeaponProf.class, one);
	}

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

}
