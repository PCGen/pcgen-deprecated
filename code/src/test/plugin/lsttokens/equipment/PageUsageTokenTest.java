package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.Equipment;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractFormulaTokenTestCase;

public class PageUsageTokenTest extends AbstractFormulaTokenTestCase<Equipment>
{

	static PageUsageToken token = new PageUsageToken();
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
	public FormulaKey getFormulaKey()
	{
		return FormulaKey.PAGE_USAGE;
	}
}
