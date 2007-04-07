package pcgen.cdom.formula;

import pcgen.base.formula.Resolver;
import pcgen.cdom.mode.Size;

public class FixedSizeResolver implements Resolver<Size>
{

	private final Size size;

	public FixedSizeResolver(Size s)
	{
		size = s;
	}

	public Size resolve()
	{
		return size;
	}

	public String toLSTFormat()
	{
		return size.toLSTFormat();
	}

}
