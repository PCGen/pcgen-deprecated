package pcgen.cdom.helper;

import pcgen.base.formula.Formula;
import pcgen.cdom.inst.CDOMStat;

public class StatLock
{

	private final CDOMStat lockedStat;
	private final Formula lockValue;

	public StatLock(CDOMStat stat, Formula f)
	{
		lockedStat = stat;
		lockValue = f;
	}

	public CDOMStat getLockedStat()
	{
		return lockedStat;
	}

	public Formula getLockValue()
	{
		return lockValue;
	}

	@Override
	public int hashCode()
	{
		return lockValue.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof StatLock)
		{
			StatLock other = (StatLock) o;
			return lockValue.equals(other.lockValue)
					&& lockedStat.equals(other.lockedStat);
		}
		return false;
	}
}
