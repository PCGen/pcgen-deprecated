package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTokenTestCase;

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
	}

	@Test
	public void testInvalidNoPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "4"));
	}

	@Test
	public void testInvalidTwoPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "4|5|6"));
	}

	@Test
	public void testInvalidMinNaN() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "String|4"));
	}

	@Test
	public void testInvalidMaxNaN() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3|Str"));
	}

	@Test
	public void testInvalidMinNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "-4|5"));
	}

	@Test
	public void testInvalidMaxNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "6|-7"));
	}

	@Test
	public void testInvalidMaxLTMin() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "7|3"));
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
