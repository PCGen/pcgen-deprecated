package plugin.pretokens.test;

import pcgen.core.Equipment;
import pcgen.core.prereq.PrerequisiteTest;

public class PreEquipBothTesterTest extends AbstractCDOMPreEquipTestCase
{

	PreEquipBothTester tester = new PreEquipBothTester();

	@Override
	public int getFalseLocation()
	{
		return Equipment.EQUIPPED_SECONDARY;
	}

	@Override
	public String getKind()
	{
		return "EQUIPBOTH";
	}

	@Override
	public int getProperLocation()
	{
		return Equipment.EQUIPPED_BOTH;
	}

	@Override
	public PrerequisiteTest getTest()
	{
		return tester;
	}

}
