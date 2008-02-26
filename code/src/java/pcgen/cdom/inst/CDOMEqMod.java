package pcgen.cdom.inst;

import pcgen.cdom.base.CDOMObject;

public class CDOMEqMod extends CDOMObject
{

	@Override
	public int hashCode()
	{
		String name = this.getKeyName();
		return name == null ? 0 : name.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMEqMod)
		{
			CDOMEqMod other = (CDOMEqMod) o;
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}
}
