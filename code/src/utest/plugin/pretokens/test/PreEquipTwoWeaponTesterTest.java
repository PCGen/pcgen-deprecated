package plugin.pretokens.test;

import pcgen.core.Equipment;
import pcgen.core.prereq.PrerequisiteTest;

public class PreEquipTwoWeaponTesterTest extends AbstractCDOMPreEquipTestCase
{

	PreEquipTwoWeaponTester tester = new PreEquipTwoWeaponTester();

	@Override
	public int getFalseLocation()
	{
		return Equipment.EQUIPPED_PRIMARY;
	}

	@Override
	public String getKind()
	{
		return "EQUIPTWOWEAPON";
	}

	@Override
	public int getProperLocation()
	{
		return Equipment.EQUIPPED_TWO_HANDS;
	}

	@Override
	public PrerequisiteTest getTest()
	{
		return tester;
	}

}
