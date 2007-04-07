package pcgen.cdom.formula;

import pcgen.base.formula.Formula;
import pcgen.base.formula.Resolver;
import pcgen.cdom.mode.Size;

public class FormulaSizeResolver implements Resolver<Size>
{

	private final Formula size;

	public FormulaSizeResolver(Formula s)
	{
		size = s;
	}

	public Size resolve()
	{
		//TODO Need to define how this will happen
		return null;
	}

	public String toLSTFormat()
	{
		return size.toString();
	}

}
