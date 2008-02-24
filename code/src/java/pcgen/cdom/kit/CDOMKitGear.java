package pcgen.cdom.kit;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.cdom.inst.CDOMEquipment;

public class CDOMKitGear extends AbstractCDOMKitObject
{

	private String location;
	private Formula quantity;
	private String size;
	private Map<CDOMReference<CDOMEqMod>, AssociatedPrereqObject> eqModMap = null;
	private CDOMReference<CDOMEquipment> equipment;
	private Integer maxCost;

	public Integer getMaxCost()
	{
		return maxCost;
	}

	public void setMaxCost(Integer maxCost)
	{
		this.maxCost = maxCost;
	}

	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public Formula getQuantity()
	{
		return quantity;
	}

	public void setQuantity(Formula quantity)
	{
		this.quantity = quantity;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public void addEqMod(CDOMReference<CDOMEqMod> eqMod,
			AssociatedPrereqObject edge)
	{
		if (eqModMap == null)
		{
			eqModMap = new HashMap<CDOMReference<CDOMEqMod>, AssociatedPrereqObject>();
		}
		eqModMap.put(eqMod, edge);
	}

	public boolean hasEqMods()
	{
		return eqModMap != null && !eqModMap.isEmpty();
	}

	public Collection<CDOMReference<CDOMEqMod>> getEqMods()
	{
		return Collections.unmodifiableSet(eqModMap.keySet());
	}

	public AssociatedPrereqObject getAssoc(CDOMReference<CDOMEqMod> eqmod)
	{
		return eqModMap.get(eqmod);
	}

	public CDOMReference<CDOMEquipment> getEquipment()
	{
		return equipment;
	}

	public void setEquipment(CDOMReference<CDOMEquipment> equipment)
	{
		this.equipment = equipment;
	}
}
