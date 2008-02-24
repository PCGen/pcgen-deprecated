package pcgen.cdom.kit;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMRace;

public class CDOMKitRace extends AbstractCDOMKitObject
{
	private CDOMReference<CDOMRace> race;

	public CDOMReference<CDOMRace> getRace()
	{
		return race;
	}

	public void setRace(CDOMReference<CDOMRace> race)
	{
		this.race = race;
	}
}
