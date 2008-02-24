package pcgen.cdom.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.inst.CDOMAlignment;

public class CDOMKitAlignment extends AbstractCDOMKitObject
{

	private List<CDOMAlignment> alignmentList;

	public void addAlignment(CDOMAlignment ref)
	{
		if (alignmentList == null)
		{
			alignmentList = new ArrayList<CDOMAlignment>();
		}
		alignmentList.add(ref);
	}

	public Collection<CDOMAlignment> getAlignments()
	{
		return alignmentList == null ? null : Collections
				.unmodifiableList(alignmentList);
	}

}
