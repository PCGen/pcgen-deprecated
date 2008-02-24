package pcgen.cdom.inst;

import pcgen.cdom.base.CDOMObject;

public class CDOMTemplate extends CDOMObject
{

	public CDOMTemplate getPseudoTemplate(String name)
	{
		CDOMTemplate pt = new CDOMTemplate();
		pt.setName(name);
		return pt;
	}

	@Override
	public int hashCode()
	{
		String name = this.getDisplayName();
		return name == null ? 0 : name.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMTemplate)
		{
			CDOMTemplate other = (CDOMTemplate) o;
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}
}
