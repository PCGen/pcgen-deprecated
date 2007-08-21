package pcgen.cdom.helper;

import pcgen.util.enumeration.AttackType;

public class AttackCycle
{

	private final AttackType quality;
	private final String value;

	public AttackCycle(AttackType key, String val)
	{
		quality = key;
		value = val;
	}

	public AttackType getAttackType()
	{
		return quality;
	}

	public String getValue()
	{
		return value;
	}

}
