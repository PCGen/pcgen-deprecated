package pcgen.rules.context;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;

public class RuntimeLoadContext extends LoadContext
{
	private final String contextType;

	public RuntimeLoadContext(DoubleKeyMapToList<CDOMObject, CDOMReference<?>, AssociatedPrereqObject> pgg)
	{
		super(new GraphContext(new ConsolidatedGraphCommitStrategy(pgg)),
			new ListContext(new ConsolidatedListCommitStrategy()),
			new ObjectContext(new ConsolidatedObjectCommitStrategy()));
		contextType = "Runtime";
	}

	/*
	 * Get the type of context we're running in (either Editor or Runtime)
	 */
	@Override
	public String getContextType()
	{
		return contextType;
	}

}
