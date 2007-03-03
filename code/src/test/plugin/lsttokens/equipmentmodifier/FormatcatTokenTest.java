package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.cdom.enumeration.EqModFormatCat;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class FormatcatTokenTest extends AbstractTokenTestCase<EquipmentModifier>
{

	static FormatcatToken token = new FormatcatToken();
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
		assertTrue(token.parse(primaryContext, primaryProf, "FRONT"));
		assertEquals(EqModFormatCat.FRONT, primaryProf.get(ObjectKey.FORMAT));
		internalTestInvalidInputString(EqModFormatCat.FRONT);
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(token.parse(primaryContext, primaryProf, "Always"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		//Note case sensitivity
		assertFalse(token.parse(primaryContext, primaryProf, "Middle"));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "FRONT"));
		assertEquals(EqModFormatCat.FRONT, primaryProf.get(ObjectKey.FORMAT));
		assertTrue(token.parse(primaryContext, primaryProf, "MIDDLE"));
		assertEquals(EqModFormatCat.MIDDLE, primaryProf.get(ObjectKey.FORMAT));
		assertTrue(token.parse(primaryContext, primaryProf, "PARENS"));
		assertEquals(EqModFormatCat.PARENS, primaryProf.get(ObjectKey.FORMAT));
	}

	@Test
	public void testRoundRobinFront() throws PersistenceLayerException
	{
		runRoundRobin("FRONT");
	}

	@Test
	public void testRoundRobinMiddle() throws PersistenceLayerException
	{
		runRoundRobin("MIDDLE");
	}

	@Test
	public void testRoundRobinParens() throws PersistenceLayerException
	{
		runRoundRobin("PARENS");
	}
}
