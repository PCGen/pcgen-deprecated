package pcgen.cdom.inst;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.cdom.base.CDOMObject;

public class CDOMSizeAdjustment extends CDOMObject implements TypeSafeConstant
{

	private static int sizeOrdinal = 0;
	
	private int ordinal;
	
	public CDOMSizeAdjustment()
	{
		super();
		ordinal = sizeOrdinal++;
	}

	public int getOrdinal()
	{
		return ordinal;
	}

	@Override
	public int hashCode()
	{
		String name = getDisplayName();
		return name == null ? 0 : name.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMSizeAdjustment)
		{
			CDOMSizeAdjustment other = (CDOMSizeAdjustment) o;
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}
}
