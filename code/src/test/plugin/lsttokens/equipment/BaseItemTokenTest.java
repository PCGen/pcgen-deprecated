package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractItemTokenTestCase;

public class BaseItemTokenTest extends
		AbstractItemTokenTestCase<Equipment, Equipment>
{

	static BaseitemToken token = new BaseitemToken();
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

	@Override
	public Class<Equipment> getTargetClass()
	{
		return Equipment.class;
	}

}
