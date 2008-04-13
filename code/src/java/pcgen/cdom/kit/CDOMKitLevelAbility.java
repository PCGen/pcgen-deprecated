package pcgen.cdom.kit;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMPCClass;

public class CDOMKitLevelAbility extends AbstractCDOMKitObject
{
	private CDOMReference<CDOMPCClass> applyClass;
	private Integer applyLevel;

	public void setApplyClass(CDOMReference<CDOMPCClass> cl)
	{
		applyClass = cl;
	}

	public void setApplyLevel(Integer lvl)
	{
		applyLevel = lvl;
	}

	public CDOMReference<CDOMPCClass> getApplyClass()
	{
		return applyClass;
	}

	public Integer getApplyLevel()
	{
		return applyLevel;
	}

}
