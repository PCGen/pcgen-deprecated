package pcgen.cdom.kit;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMAbility;

public class CDOMKitAbility extends AbstractCDOMKitObject
{

	private Boolean free;
	private Integer count;
	private CDOMReference<CDOMAbility> ability;

	public Integer getCount()
	{
		return count;
	}

	public void setCount(Integer count)
	{
		this.count = count;
	}

	public Boolean getFree()
	{
		return free;
	}

	public void setFree(Boolean free)
	{
		this.free = free;
	}

	public CDOMReference<CDOMAbility> getAbility()
	{
		return ability;
	}

	public void setAbility(CDOMReference<CDOMAbility> ab)
	{
		this.ability = ab;
	}

}
