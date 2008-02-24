package pcgen.cdom.inst;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;

public class CDOMStat extends CDOMObject
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
		if (o instanceof CDOMStat)
		{
			CDOMStat other = (CDOMStat) o;
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}

	@Override
	public String getLSTformat()
	{
		String abb = get(StringKey.ABB);
		if (abb == null)
		{
			return super.getLSTformat();
		}
		return abb;
	}
	
	
}
