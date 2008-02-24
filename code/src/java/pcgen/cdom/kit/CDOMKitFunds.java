package pcgen.cdom.kit;

import pcgen.base.formula.Formula;

public class CDOMKitFunds extends AbstractCDOMKitObject
{
	private String name;
	private Formula quantity;

	public Formula getQuantity()
	{
		return quantity;
	}

	public void setQuantity(Formula quantity)
	{
		this.quantity = quantity;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
