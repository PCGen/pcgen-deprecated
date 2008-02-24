package pcgen.cdom.formula;

import pcgen.base.formula.Formula;
import pcgen.base.formula.Resolver;
import pcgen.cdom.inst.CDOMSizeAdjustment;

public class FormulaSizeResolver implements Resolver<CDOMSizeAdjustment>
{

	private final Formula size;

	public FormulaSizeResolver(Formula s)
	{
		size = s;
	}

	public CDOMSizeAdjustment resolve()
	{
		// TODO Need to define how this will happen
		return null;
	}

	public String toLSTFormat()
	{
		return size.toString();
	}

	@Override
	public int hashCode()
	{
		return size.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof FormulaSizeResolver
			&& size.equals(((FormulaSizeResolver) o).size);
	}
}
