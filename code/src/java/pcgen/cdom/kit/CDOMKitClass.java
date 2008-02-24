package pcgen.cdom.kit;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSubClass;

public class CDOMKitClass extends AbstractCDOMKitObject
{

	private Formula level;
	private CDOMReference<CDOMPCClass> pcclass;
	private CDOMReference<CDOMSubClass> subclass;

	public Formula getLevel()
	{
		return level;
	}

	public void setLevel(Formula level)
	{
		this.level = level;
	}

	public CDOMReference<CDOMSubClass> getSubClass()
	{
		return subclass;
	}

	public void setSubClass(CDOMReference<CDOMSubClass> subclass)
	{
		this.subclass = subclass;
	}

	public CDOMReference<CDOMPCClass> getPcclass()
	{
		return pcclass;
	}

	public void setPcclass(CDOMReference<CDOMPCClass> pcclass)
	{
		this.pcclass = pcclass;
	}

}
