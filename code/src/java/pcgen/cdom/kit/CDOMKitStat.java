package pcgen.cdom.kit;

import java.util.HashMap;
import java.util.Map;

import pcgen.base.formula.Formula;
import pcgen.cdom.inst.CDOMStat;

public class CDOMKitStat extends AbstractCDOMKitObject
{
	private Map<CDOMStat, Formula> statMap = new HashMap<CDOMStat, Formula>();

	public void addStat(CDOMStat stat, Formula statValue)
	{
		statMap.put(stat, statValue);
	}

}
