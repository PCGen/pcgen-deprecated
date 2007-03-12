package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTypeSafeListTestCase;

public class ReplacesTokenTest extends
		AbstractTypeSafeListTestCase<EquipmentModifier>
{
	static ReplacesToken token = new ReplacesToken();
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
	public Object getConstant(String string)
	{
		return primaryContext.ref.getCDOMReference(EquipmentModifier.class,
			string);
	}

	@Override
	public char getJoinCharacter()
	{
		return ',';
	}

	@Override
	public ListKey<?> getListKey()
	{
		return ListKey.REPLACED_KEYS;
	}

}
