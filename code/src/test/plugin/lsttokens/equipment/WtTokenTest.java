package plugin.lsttokens.equipment;

import java.util.Set;

import org.junit.Test;

import pcgen.cdom.content.Weight;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTokenTestCase;

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
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"TYPE=TestType"));
	}

	@Test
	public void testInvalidInputNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "-1"));
	}

	@Test
	public void testInvalidInputFormula() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
	}

	@Test
	public void testInvalidInputFraction() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1/2"));
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
		assertEquals(new Weight(4.5), edge.getNodeAt(1));
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
		assertEquals(new Weight(5), edge.getNodeAt(1));
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
		assertEquals(new Weight(0), edge.getNodeAt(1));
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
