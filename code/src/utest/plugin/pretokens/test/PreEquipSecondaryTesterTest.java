package plugin.pretokens.test;

import pcgen.core.Equipment;
import pcgen.core.prereq.PrerequisiteTest;

public class PreEquipSecondaryTesterTest extends AbstractCDOMPreEquipTestCase
{

	PreEquipSecondaryTester tester = new PreEquipSecondaryTester();

	@Override
	public int getFalseLocation()
	{
		return Equipment.EQUIPPED_PRIMARY;
	}

	@Override
	public String getKind()
	{
		return "EQUIPSECONDARY";
	}

	@Override
	public int getProperLocation()
	{
		return Equipment.EQUIPPED_SECONDARY;
	}

	@Override
	public PrerequisiteTest getTest()
	{
		return tester;
	}

}
