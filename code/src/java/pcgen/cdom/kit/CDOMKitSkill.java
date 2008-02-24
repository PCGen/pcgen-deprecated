package pcgen.cdom.kit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSkill;

public class CDOMKitSkill extends AbstractCDOMKitObject
{

	private BigDecimal rank;
	private Integer count;
	private Boolean free;
	private List<CDOMReference<CDOMSkill>> skillList;
	private CDOMReference<CDOMPCClass> pcClass;

	public Boolean getFree()
	{
		return free;
	}

	public void setFree(Boolean free)
	{
		this.free = free;
	}

	public Integer getCount()
	{
		return count;
	}

	public void setCount(Integer count)
	{
		this.count = count;
	}

	public BigDecimal getRank()
	{
		return rank;
	}

	public void setRank(BigDecimal rank)
	{
		this.rank = rank;
	}

	public void addSkill(CDOMReference<CDOMSkill> ref)
	{
		if (skillList == null)
		{
			skillList = new ArrayList<CDOMReference<CDOMSkill>>();
		}
		skillList.add(ref);
	}

	public Collection<CDOMReference<CDOMSkill>> getSkills()
	{
		return skillList == null ? null : Collections
				.unmodifiableList(skillList);
	}

	public void setPcclass(CDOMReference<CDOMPCClass> ref)
	{
		pcClass = ref;
	}

	public CDOMReference<CDOMPCClass> getPcclass()
	{
		return pcClass;
	}

}
