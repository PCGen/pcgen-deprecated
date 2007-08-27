package pcgen.cdom.helper;

import pcgen.util.enumeration.AttackType;

public class AttackCycle
{

	private final AttackType type;
	private final String value;

	public AttackCycle(AttackType key, String val)
	{
		type = key;
		value = val;
	}

	public AttackType getAttackType()
	{
		return type;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return type.hashCode() ^ value.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AttackCycle)
		{
			AttackCycle other = (AttackCycle) o;
			return type.equals(other.type) && value.equals(other.value);
		}
		return false;
	}
}
