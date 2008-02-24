package pcgen.cdom.formula;

import pcgen.base.formula.Resolver;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.CDOMSizeAdjustment;

public class FixedSizeResolver implements Resolver<CDOMSizeAdjustment>
{

	private final CDOMSizeAdjustment size;

	public FixedSizeResolver(CDOMSizeAdjustment s)
	{
		size = s;
	}

	public CDOMSizeAdjustment resolve()
	{
		return size;
	}

	public String toLSTFormat()
	{
		return size.get(StringKey.ABB);
	}

	@Override
	public int hashCode()
	{
		return size.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof FixedSizeResolver
			&& size.equals(((FixedSizeResolver) o).size);
	}
}
