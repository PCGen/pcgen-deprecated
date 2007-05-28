package plugin.pretokens.test;

import pcgen.core.Equipment;
import pcgen.core.prereq.PrerequisiteTest;

public class PreEquipPrimaryTesterTest extends AbstractCDOMPreEquipTestCase
{

	PreEquipPrimaryTester tester = new PreEquipPrimaryTester();

	@Override
	public int getFalseLocation()
	{
		return Equipment.EQUIPPED_SECONDARY;
	}

	@Override
	public String getKind()
	{
		return "EQUIPPRIMARY";
	}

	@Override
	public int getProperLocation()
	{
		return Equipment.EQUIPPED_PRIMARY;
	}

	@Override
	public PrerequisiteTest getTest()
	{
		return tester;
	}

}
