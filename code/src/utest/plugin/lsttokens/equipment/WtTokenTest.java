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

import java.math.BigDecimal;
import java.util.Set;

import org.junit.Test;

import pcgen.cdom.content.Weight;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class WtTokenTest extends AbstractTokenTestCase<Equipment>
{
	static WtToken token = new WtToken();
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
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE=TestType"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "-1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputFormula() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputFraction() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1/2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputDecimal() throws PersistenceLayerException
	{
		Set<PCGraphEdge> edges;
		PCGraphEdge edge;
		assertTrue(getToken().parse(primaryContext, primaryProf, "4.5"));
		edges =
				primaryContext.graph.getChildLinksFromToken(getToken()
					.getTokenName(), primaryProf);
		assertEquals(1, edges.size());
		edge = edges.iterator().next();
		assertEquals(new Weight(new BigDecimal(4.5)), edge.getNodeAt(1));
	}

	@Test
	public void testValidInputInteger() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "5"));
		Set<PCGraphEdge> edges =
				primaryContext.graph.getChildLinksFromToken(getToken()
					.getTokenName(), primaryProf);
		assertEquals(1, edges.size());
		PCGraphEdge edge = edges.iterator().next();
		assertEquals(new Weight(new BigDecimal(5)), edge.getNodeAt(1));
	}

	@Test
	public void testValidInputZero() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "0"));
		Set<PCGraphEdge> edges =
				primaryContext.graph.getChildLinksFromToken(getToken()
					.getTokenName(), primaryProf);
		assertEquals(1, edges.size());
		PCGraphEdge edge = edges.iterator().next();
		assertEquals(new Weight(BigDecimal.ZERO), edge.getNodeAt(1));
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
