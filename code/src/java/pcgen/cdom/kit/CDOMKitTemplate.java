package pcgen.cdom.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.cdom.reference.CDOMSingleRef;

public class CDOMKitTemplate extends AbstractCDOMKitObject
{

	private CDOMReference<CDOMTemplate> template;
	private List<CDOMReference<CDOMTemplate>> subTemplateList;

	public void addSubTemplate(CDOMSingleRef<CDOMTemplate> ref)
	{
		if (subTemplateList == null)
		{
			subTemplateList = new ArrayList<CDOMReference<CDOMTemplate>>();
		}
		subTemplateList.add(ref);
	}

	public Collection<CDOMReference<CDOMTemplate>> getSubTemplates()
	{
		return subTemplateList == null ? null : Collections
				.unmodifiableList(subTemplateList);
	}

	public void setTemplate(CDOMSingleRef<CDOMTemplate> ref)
	{
		template = ref;
	}

	public CDOMReference<CDOMTemplate> getTemplate()
	{
		return template;
	}
}
