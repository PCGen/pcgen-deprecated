package pcgen.cdom.helper;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.core.CompanionList;

public class FollowerLimit
{

	private final CDOMSimpleSingleRef<CompanionList> ref;
	private final Formula f;

	public FollowerLimit(CDOMSimpleSingleRef<CompanionList> cl, Formula limit)
	{
		ref = cl;
		f = limit;
	}

	public CDOMSimpleSingleRef<CompanionList> getCompanionList()
	{
		return ref;
	}

	public Formula getValue()
	{
		return f;
	}

}
