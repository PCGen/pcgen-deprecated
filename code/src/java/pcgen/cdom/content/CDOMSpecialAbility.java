package pcgen.cdom.content;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;

public class CDOMSpecialAbility extends ConcretePrereqObject implements
		LSTWriteable
{

	private final String baseName;
	private String name;

	public CDOMSpecialAbility(String firstToken)
	{
		baseName = firstToken;
	}

	@Override
	public int hashCode()
	{
		return baseName == null ? 0 : baseName.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMSpecialAbility)
		{
			CDOMSpecialAbility other = (CDOMSpecialAbility) o;
			return baseName.equals(other.baseName)
					&& other.equalsPrereqObject(this);
		}
		return false;
	}

	public String getLSTformat()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
