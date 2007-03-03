package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class CostdoubleTokenTest extends AbstractTokenTestCase<EquipmentModifier>
{

	static CostdoubleToken token = new CostdoubleToken();
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
		internalTestInvalidInputString(null);
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.COST_DOUBLE));
		internalTestInvalidInputString(Boolean.TRUE);
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.COST_DOUBLE));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(ObjectKey.COST_DOUBLE));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.COST_DOUBLE));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.COST_DOUBLE));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.COST_DOUBLE));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.COST_DOUBLE));
		assertTrue(token.parse(primaryContext, primaryProf, "NO"));
		assertEquals(Boolean.FALSE, primaryProf.get(ObjectKey.COST_DOUBLE));
		//We're nice enough to be case insensitive here...
		assertTrue(token.parse(primaryContext, primaryProf, "YeS"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.COST_DOUBLE));
		assertTrue(token.parse(primaryContext, primaryProf, "Yes"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.COST_DOUBLE));
		assertTrue(token.parse(primaryContext, primaryProf, "No"));
		assertEquals(Boolean.FALSE, primaryProf.get(ObjectKey.COST_DOUBLE));
	}

	@Test
	public void testRoundRobinDisplay() throws PersistenceLayerException
	{
		runRoundRobin("YES");
	}

	@Test
	public void testRoundRobinExport() throws PersistenceLayerException
	{
		runRoundRobin("NO");
	}
}
