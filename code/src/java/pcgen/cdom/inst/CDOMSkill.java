package pcgen.cdom.inst;

import pcgen.cdom.base.CDOMObject;

public class CDOMSkill extends CDOMObject
{

	@Override
	public int hashCode()
	{
		String name = this.getDisplayName();
		return name == null ? 0 : name.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMSkill)
		{
			CDOMSkill other = (CDOMSkill) o;
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}
}
