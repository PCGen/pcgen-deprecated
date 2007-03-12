package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractFormulaTokenTestCase;

public class CostTokenTest extends
		AbstractFormulaTokenTestCase<EquipmentModifier>
{

	static CostToken token = new CostToken();
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

	@Override
	public FormulaKey getFormulaKey()
	{
		return FormulaKey.COST;
	}
}
