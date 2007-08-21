package pcgen.persistence;

import pcgen.cdom.graph.PCGenGraph;

public class RuntimeLoadContext extends LoadContext
{
	private final GraphContext graph;

	private final ListContext list;

	private final ObjectContext obj;

	private final String contextType;

	public RuntimeLoadContext(PCGenGraph pgg)
	{
		graph = new RuntimeGraphContext(pgg);
		obj = new RuntimeObjectContext();
		list = new RuntimeListContext();
		contextType = "Runtime";
	}

	@Override
	public GraphContext getGraphContext()
	{
		return graph;
	}

	@Override
	public ObjectContext getObjectContext()
	{
		return obj;
	}

	@Override
	public ListContext getListContext()
	{
		return list;
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
