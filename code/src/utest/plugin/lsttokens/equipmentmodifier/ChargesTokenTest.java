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

import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class ChargesTokenTest extends AbstractTokenTestCase<EquipmentModifier>
{

	static ChargesToken token = new ChargesToken();
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
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTwoPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "4|5|6"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidMinNaN() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "String|4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidMaxNaN() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3|Str"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidMinNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "-4|5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidMaxNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "6|-7"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidMaxLTMin() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "7|3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("4|10");
	}

	@Test
	public void testRoundRobinMatching() throws PersistenceLayerException
	{
		runRoundRobin("10|10");
	}
}
