package pcgen.cdom.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.inst.CDOMKit;

public class CDOMKitKit extends AbstractCDOMKitObject
{
	private List<CDOMReference<CDOMKit>> kitList;

	public void addKit(CDOMSingleRef<CDOMKit> ref)
	{
		if (kitList == null)
		{
			kitList = new ArrayList<CDOMReference<CDOMKit>>();
		}
		kitList.add(ref);
	}

	public Collection<CDOMReference<CDOMKit>> getKits()
	{
		return kitList == null ? null : Collections.unmodifiableList(kitList);
	}
}
