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
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class ContainsTokenTest extends AbstractTokenTestCase<Equipment>
{

	static ContainsToken token = new ContainsToken();
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
	public CDOMToken<Equipment> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputNaN() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "X4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputReducingFirstNaN()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "X4%60"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputReducingSecondNaN()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "50%X4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputSplatReducing()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*50%40"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTwoPercent() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "50%40%30"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingSplat()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "4*"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmbeddedSplat()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5*4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNaNTyped() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "X4|Any=25"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputReducingFirstNaNTyped()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "X4%60|Any=25"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputReducingSecondNaNTyped()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "50%X4|Any=25"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputSplatReducingTyped()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*50%40|Any=25"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTwoPercentTyped()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "50%40%30|Any=25"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingSplatTyped()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "4*|Any=25"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmbeddedSplatTyped()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5*4|Any=25"));
		assertTrue(primaryGraph.isEmpty());
	}

	public void testInvalidNoCapacity() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Cookies"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidCapacityNoTypeQuantity()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5|Any="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidCapacityZeroQuantity()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5|Cookies=0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidCapacityNegativeQuantity()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5|Cookies=-10"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidCapacityTypeQuantityNaN()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5|Any=4X"));
		assertTrue(primaryGraph.isEmpty());
	}

	public void testInvalidCapacityUselessPipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidCapacityTypeLeadingDoublePipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5||Any=4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidCapacityTypeTrailingPipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5|Any=4|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidCapacityTypeDoubleEquals()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5|Any=4=3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidCapacityTypeMiddlePipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"5|Cookies=4||Crackers=3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidWeightlessNoTypeQuantity()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*5|Any="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidWeightlessZeroQuantity()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*5|Cookies=0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidWeightlessNegativeQuantity()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*5|Cookies=-10"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidWeightlessTypeQuantityNaN()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*5|Any=4X"));
		assertTrue(primaryGraph.isEmpty());
	}

	public void testInvalidWeightlessUselessPipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*5|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidWeightlessTypeLeadingDoublePipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*5||Any=4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidWeightlessTypeTrailingPipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*5|Any=4|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidWeightlessTypeDoubleEquals()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "*5|Any=4=3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidWeightlessTypeMiddlePipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"*5|Cookies=4||Crackers=3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReducedNoTypeQuantity()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "40%30|Any="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReducedTypeQuantityNaN()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "40%30|Any=4X"));
		assertTrue(primaryGraph.isEmpty());
	}

	public void testInvalidReducedUselessPipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "40%30|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReducedTypeLeadingDoublePipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "40%30||Any=4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReducedTypeTrailingPipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "40%30|Any=4|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReducedZeroQuantity()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "40%30|Cookies=0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReducedNegativeQuantity()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"40%30|Cookies=-10"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReducedTypeDoubleEquals()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "40%30|Any=4=3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidReducedTypeMiddlePipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"40%30|Cookies=4||Crackers=3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		this.runRoundRobin("500");
	}

	@Test
	public void testRoundRobinSimpleWeightless()
		throws PersistenceLayerException
	{
		this.runRoundRobin("*500");
	}

	@Test
	public void testRoundRobinSimpleReducing() throws PersistenceLayerException
	{
		this.runRoundRobin("50%40");
	}

	@Test
	public void testRoundRobinTypeLimited() throws PersistenceLayerException
	{
		this.runRoundRobin("50|Cookies");
	}

	@Test
	public void testRoundRobinTypeLimitMix() throws PersistenceLayerException
	{
		this.runRoundRobin("5|Cookies=4|Crackers");
	}

	@Test
	public void testRoundRobinWeightlessTypeLimitMix()
		throws PersistenceLayerException
	{
		this.runRoundRobin("*15|Cookies=4|Crackers");
	}

	@Test
	public void testRoundRobinLimitedReducing()
		throws PersistenceLayerException
	{
		this.runRoundRobin("50%30|Any=25");
	}

	@Test
	public void testRoundRobinCountLimitedReducing()
		throws PersistenceLayerException
	{
		this.runRoundRobin("25%-1|Any=100");
	}

	@Test
	public void testRoundRobinCountLimitedCursedAdding()
		throws PersistenceLayerException
	{
		this.runRoundRobin("-35%-1|Any=100");
	}

	@Test
	public void testRoundRobinTypeQuantityLimited()
		throws PersistenceLayerException
	{
		this.runRoundRobin("500|Potions=100");
	}

	@Test
	public void testRoundRobinTypeUnlimited() throws PersistenceLayerException
	{
		this.runRoundRobin("-1");
	}

	@Test
	public void testRoundRobinTypeCountLimited()
		throws PersistenceLayerException
	{
		this.runRoundRobin("-1|Any=100");
	}

	@Test
	public void testRoundRobinTypeComplexWeightUnlimited()
		throws PersistenceLayerException
	{
		this.runRoundRobin("-1|Total=10|Paper=10|Scroll=10");
	}
}
