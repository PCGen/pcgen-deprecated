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
package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.cdom.inst.CDOMEqMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class DamageTokenTest extends AbstractTokenTestCase<CDOMEqMod>
{

	public static DamageToken token = new DamageToken();
	static CDOMTokenLoader<CDOMEqMod> loader = new CDOMTokenLoader<CDOMEqMod>(
			CDOMEqMod.class);

	@Override
	public CDOMPrimaryToken<CDOMEqMod> getToken()
	{
		return token;
	}

	@Override
	public Class<CDOMEqMod> getCDOMClass()
	{
		return CDOMEqMod.class;
	}

	@Override
	public CDOMLoader<CDOMEqMod> getLoader()
	{
		return loader;
	}

	//TODO Should Damage be tightened up?
	// @Test
	// public void testInvalidStringInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
	// }
	//
	// @Test
	// public void testInvalidTypeInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf,
	// "TYPE=TestType"));
	// }
	//
	// @Test
	// public void testInvalidDecimalInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "4.5"));
	// }
	//
	// @Test
	// public void testInvalidFractionInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "1/2"));
	// }
	//
	// @Test
	// public void testInvalidFunctionInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
	// }
	//
	// @Test
	// public void testInvalidNegativeInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "-1"));
	// }
	//
	// @Test
	// public void testInvalidZeroInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "0"));
	// }
	//
	// @Test
	// public void testInvalidTimesNegativeInput()
	// throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "x-1"));
	// }
	//
	// @Test
	// public void testInvalidTimesZeroInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "x0"));
	// }
	//
	// @Test
	// public void testInvalidNoTimesInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, "3"));
	// }
	//
	// @Test
	// public void testInvalidEmptyInput() throws PersistenceLayerException
	// {
	// assertFalse(getToken().parse(primaryContext, primaryProf, ""));
	// }

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		runRoundRobin("2");
	}

	@Test
	public void testRoundRobinDeeSix() throws PersistenceLayerException
	{
		runRoundRobin("1d6");
	}

	@Test
	public void testRoundRobinDash() throws PersistenceLayerException
	{
		runRoundRobin("-");
	}
}
