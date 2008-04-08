package pcgen.cdom.inst;

import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;

public class CDOMTemplate extends CDOMObject
{
	
	private Map<String, CDOMTemplate> pseudoTemplateMap = new HashMap<String, CDOMTemplate>();

	public CDOMTemplate getPseudoTemplate(String name)
	{
		CDOMTemplate pt = pseudoTemplateMap.get(name);
		if (pt == null)
		{
			pt = new CDOMTemplate();
			pt.setName(name);
			pseudoTemplateMap.put(name, pt);
		}
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
