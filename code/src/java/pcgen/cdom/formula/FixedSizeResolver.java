package pcgen.cdom.formula;

import pcgen.base.formula.Resolver;
import pcgen.core.SizeAdjustment;

public class FixedSizeResolver implements Resolver<SizeAdjustment>
{

	private final SizeAdjustment size;

	public FixedSizeResolver(SizeAdjustment s)
	{
		size = s;
	}

	public SizeAdjustment resolve()
	{
		return size;
	}

	public String toLSTFormat()
	{
		return size.getAbbreviation();
	}

}
