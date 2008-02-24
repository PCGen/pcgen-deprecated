package pcgen.cdom.kit;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;

public class AbstractCDOMKitObject extends ConcretePrereqObject
{

	Formula minOption;
	Formula maxOption;
	
	public void setOptionBounds(Formula min, Formula max)
	{
		minOption = min;
		maxOption = max;
	}

	public Formula getOptionMin()
	{
		return minOption;
	}

	public Formula getOptionMax()
	{
		return maxOption;
	}

}
