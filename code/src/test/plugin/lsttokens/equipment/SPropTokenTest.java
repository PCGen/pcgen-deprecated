package plugin.lsttokens.equipment;

import org.junit.Test;

import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTextPropertyTokenTestCase;

public class SPropTokenTest extends
		AbstractTextPropertyTokenTestCase<Equipment>
{
	static SpropToken token = new SpropToken();
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
