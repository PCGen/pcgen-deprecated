package pcgen.cdom.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMDomain;

public class CDOMKitDeity extends AbstractCDOMKitObject
{

	private final List<CDOMReference<CDOMDomain>> domainList = new ArrayList<CDOMReference<CDOMDomain>>();

	private Formula count;
	private CDOMReference<CDOMDeity> deity;

	public CDOMReference<CDOMDeity> getDeity()
	{
		return deity;
	}

	public void setDeity(CDOMReference<CDOMDeity> deity)
	{
		this.deity = deity;
	}

	public Formula getCount()
	{
		return count;
	}

	public void setCount(Formula formula)
	{
		this.count = formula;
	}

	public void addDomain(CDOMReference<CDOMDomain> ref)
	{
		domainList.add(ref);
	}

	public Collection<CDOMReference<CDOMDomain>> getDomains()
	{
		return Collections.unmodifiableList(domainList);
	}

}
