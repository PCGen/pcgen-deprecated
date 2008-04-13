package pcgen.rules.persistence;

import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.rules.context.LoadContext;

public class CDOMSizeLoader extends CDOMLineLoader<CDOMSizeAdjustment>
{

	private static final Class<CDOMSizeAdjustment> SIZE_CLASS = CDOMSizeAdjustment.class;

	public CDOMSizeLoader()
	{
		super("SIZENAME", SIZE_CLASS);
	}

	@Override
	public CDOMSizeAdjustment getCDOMObject(LoadContext context, String name)
	{
		CDOMSizeAdjustment obj = context.ref.silentlyGetConstructedCDOMObject(
				SIZE_CLASS, name);
		if (obj == null)
		{
			obj = context.ref.constructCDOMObject(SIZE_CLASS, name);
		}
		return obj;
	}
}
