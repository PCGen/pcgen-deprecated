package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractStringTokenTestCase;

public class FumblerangeTokenTest extends
		AbstractStringTokenTestCase<EquipmentModifier>
{

	static FumblerangeToken token = new FumblerangeToken();
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
	public StringKey getStringKey()
	{
		return StringKey.FUMBLE_RANGE;
	}

	@Test
	public void dummyTest()
	{
		//Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}
}
