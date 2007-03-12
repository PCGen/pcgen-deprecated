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
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractListTokenTestCase;

public abstract class AbstractEqModTokenTestCase extends
		AbstractListTokenTestCase<Equipment, EquipmentModifier>
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

	@Override
	public Class<EquipmentModifier> getTargetClass()
	{
		return EquipmentModifier.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public char getJoinCharacter()
	{
		return '.';
	}

	@Override
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		// This is not invalid, because EqMod uses | for associations
	}

	@Test
	public void testInvalidMiddleNone() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"EQMOD1.NONE.EQMOD2"));
	}

	@Test
	public void testInvalidStartingNone() throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "NONE.EQMOD2"));
	}

	@Test
	public void testInvalidEndingNone() throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "EQMOD2.NONE"));
	}

	@Test
	public void testInvalidEmptyAssociation() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "EQMOD2|"));
	}

	@Test
	public void testInvalidTrailingAssociation()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"EQMOD2|Assoc|"));
	}

	@Test
	public void testInvalidEmptyModAssociation()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"|Assoc|Assoc2"));
	}

	@Test
	public void testInvalidEmptySecondModAssociation()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"MOD1.|Assoc|Assoc2"));
	}

	@Test
	public void testInvalidEmptySecondModAfterAssociation()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"MOD1|ModAssoc.|Assoc|Assoc2"));
	}

	@Test
	public void testInvalidEmptyComplexAssociation()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"MOD1|ModAssoc[]"));
	}

	@Test
	public void testInvalidNoOpenBracketComplexAssociation()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"MOD1|ModAssoc Assoc]"));
	}

	@Test
	public void testInvalidTwoOpenBracketComplexAssociation()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"MOD1|ModAssoc[[Assoc]"));
	}

	@Test
	public void testInvalidDoubleBarAssociation()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"EQMOD2|Assoc||Assoc2"));
	}

	public void testRoundRobinOnlyAssociation()
		throws PersistenceLayerException
	{
		runRoundRobin("EQMOD2|9500");
	}

	public void testRoundRobinComplexAssociation()
		throws PersistenceLayerException
	{
		runRoundRobin("EQMOD2|COST[9500]");
	}

	// public void testRoundRobinInnerBracketAssociation()
	// throws PersistenceLayerException
	// {
	// runRoundRobin("EQMOD2|COST[[9500]]");
	// }

	public void testRoundRobinComplexMultipleAssociation()
		throws PersistenceLayerException
	{
		runRoundRobin("EQMOD2|COST[9500]PLUS[+1]");
	}

}
