package pcgen.cdom.kit;

import pcgen.base.formula.Formula;

public class CDOMKitSelect extends AbstractCDOMKitObject
{
	private Formula select;

	public Formula getSelect()
	{
		return select;
	}

	public void setSelect(Formula select)
	{
		this.select = select;
	}
}
