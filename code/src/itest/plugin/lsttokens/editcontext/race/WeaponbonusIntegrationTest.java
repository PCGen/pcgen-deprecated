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
package plugin.lsttokens.editcontext.race;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.Race;
import pcgen.core.WeaponProf;
import pcgen.core.WeaponProfList;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.RaceLoader;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;
import plugin.lsttokens.race.WeaponbonusToken;

public class WeaponbonusIntegrationTest extends
		AbstractListIntegrationTestCase<Race, WeaponProf>
{

	static WeaponbonusToken token = new WeaponbonusToken();
	static RaceLoader loader = new RaceLoader();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		/*
		 * FIXME This construction of *Starting should be unnecessary
		 */
		primaryContext.ref.constructCDOMObject(WeaponProfList.class,
			"*Starting");
		secondaryContext.ref.constructCDOMObject(WeaponProfList.class,
			"*Starting");
	}

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public LstObjectFileLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Race> getToken()
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
		return true;
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
		return false;
	}

}
