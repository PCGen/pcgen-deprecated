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

import pcgen.cdom.inst.CDOMEqMod;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;

public abstract class AbstractEqModTokenTestCase extends
		AbstractListTokenTestCase<CDOMEquipment, CDOMEqMod>
{

	static CDOMTokenLoader<CDOMEquipment> loader = new CDOMTokenLoader<CDOMEquipment>(
			CDOMEquipment.class);

	@Override
	public Class<CDOMEquipment> getCDOMClass()
	{
		return CDOMEquipment.class;
	}

	@Override
	public CDOMLoader<CDOMEquipment> getLoader()
	{
		return loader;
	}

	@Override
	public Class<CDOMEqMod> getTargetClass()
	{
		return CDOMEqMod.class;
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
		assertFalse(parse("EQMOD1.NONE.EQMOD2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidStartingNone() throws PersistenceLayerException
	{
		assertFalse(parse("NONE.EQMOD2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEndingNone() throws PersistenceLayerException
	{
		assertFalse(parse("EQMOD2.NONE"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyAssociation() throws PersistenceLayerException
	{
		assertFalse(parse("EQMOD2|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTrailingAssociation()
		throws PersistenceLayerException
	{
		assertFalse(parse("EQMOD2|Assoc|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyModAssociation()
		throws PersistenceLayerException
	{
		assertFalse(parse("|Assoc|Assoc2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptySecondModAssociation()
		throws PersistenceLayerException
	{
		assertFalse(parse("MOD1.|Assoc|Assoc2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptySecondModAfterAssociation()
		throws PersistenceLayerException
	{
		assertFalse(parse("MOD1|ModAssoc.|Assoc|Assoc2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyComplexAssociation()
		throws PersistenceLayerException
	{
		assertFalse(parse("MOD1|ModAssoc[]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoOpenBracketComplexAssociation()
		throws PersistenceLayerException
	{
		assertFalse(parse("MOD1|ModAssoc Assoc]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTwoOpenBracketComplexAssociation()
		throws PersistenceLayerException
	{
		assertFalse(parse("MOD1|ModAssoc[[Assoc]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoubleBarAssociation()
		throws PersistenceLayerException
	{
		assertFalse(parse("EQMOD2|Assoc||Assoc2"));
		assertNoSideEffects();
	}

	public void testRoundRobinOnlyAssociation()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMEqMod.class,
			"EQMOD2");
		secondaryContext.ref.constructCDOMObject(CDOMEqMod.class,
			"EQMOD2");
		runRoundRobin("EQMOD2|9500");
	}

	public void testRoundRobinComplexAssociation()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMEqMod.class,
			"EQMOD2");
		secondaryContext.ref.constructCDOMObject(CDOMEqMod.class,
			"EQMOD2");
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
		primaryContext.ref.constructCDOMObject(CDOMEqMod.class,
			"EQMOD2");
		secondaryContext.ref.constructCDOMObject(CDOMEqMod.class,
			"EQMOD2");
		runRoundRobin("EQMOD2|COST[9500]PLUS[+1]");
	}

}
