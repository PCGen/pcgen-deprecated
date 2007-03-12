package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTextPropertyTokenTestCase;

public class SPropTokenTest extends
		AbstractTextPropertyTokenTestCase<EquipmentModifier>
{
	static SpropToken token = new SpropToken();
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
	public void testInvalidDoubleClear() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			".CLEAR|.CLEAR|Second"));
	}

	@Test
	public void testInvalidClearAsVariable() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Second|.CLEAR"));
	}
}
