package pcgen.cdom.kit;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMPCClass;

public class CDOMKitLevelAbility extends AbstractCDOMKitObject
{
	private CDOMReference<CDOMPCClass> applyClass;
	private Integer applyLevel;

	public void setClass(CDOMReference<CDOMPCClass> cl)
	{
		applyClass = cl;
	}

	public void setLevel(Integer lvl)
	{
		applyLevel = lvl;
	}

}
