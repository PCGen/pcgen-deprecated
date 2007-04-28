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

import org.junit.Test;

import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public abstract class AbstractCritMultTokenTestCase extends
		AbstractTokenTestCase<Equipment>
{

	static EquipmentLoader loader = new EquipmentLoader();

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public LstObjectFileLoader<Equipment> getLoader()
	{
		return loader;
	}

	@Test
	public void testInvalidStringInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
	}

	@Test
	public void testInvalidTypeInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE=TestType"));
	}

	@Test
	public void testInvalidDecimalInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "4.5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidFractionInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1/2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidFunctionInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNegativeInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "-1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidZeroInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTimesNegativeInput()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "x-1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTimesZeroInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "x0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoTimesInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptyInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTimesNaNInput() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "xY"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		runRoundRobin("x2");
	}

	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("x5");
	}

	@Test
	public void testRoundRobinDash() throws PersistenceLayerException
	{
		runRoundRobin("-");
	}
}
