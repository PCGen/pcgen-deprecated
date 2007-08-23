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

import java.math.BigDecimal;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class WtTokenTest extends AbstractTokenTestCase<EquipmentModifier>
{
	static WtToken token = new WtToken();
	static EquipmentModifierLoader loader = new EquipmentModifierLoader();

	@Override
	public Class<EquipmentModifier> getCDOMClass()
	{
		return EquipmentModifier.class;
	}

	@Override
	public LstObjectFileLoader<EquipmentModifier> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<EquipmentModifier> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertFalse(parse("String"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		assertFalse(parse("TYPE=TestType"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegative() throws PersistenceLayerException
	{
		assertFalse(parse("-1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputFormula() throws PersistenceLayerException
	{
		assertFalse(parse("1+3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputFraction() throws PersistenceLayerException
	{
		assertFalse(parse("1/2"));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputDecimal() throws PersistenceLayerException
	{
		assertTrue(parse("4.5"));
		BigDecimal weight = primaryProf.get(ObjectKey.WEIGHT);
		assertEquals(new BigDecimal(4.5), weight);
	}

	@Test
	public void testValidInputInteger() throws PersistenceLayerException
	{
		assertTrue(parse("5"));
		BigDecimal weight = primaryProf.get(ObjectKey.WEIGHT);
		assertEquals(new BigDecimal(5), weight);
	}

	@Test
	public void testValidInputZero() throws PersistenceLayerException
	{
		assertTrue(parse("0"));
		BigDecimal weight = primaryProf.get(ObjectKey.WEIGHT);
		assertEquals(new BigDecimal(0), weight);
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin("1");
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		runRoundRobin("0");
	}

	@Test
	public void testRoundRobinThreePointFive() throws PersistenceLayerException
	{
		runRoundRobin("3.5");
	}

}
