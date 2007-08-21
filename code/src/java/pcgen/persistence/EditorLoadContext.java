package pcgen.persistence;

import pcgen.cdom.graph.PCGenGraph;

public class EditorLoadContext extends LoadContext
{
	private final GraphContext graph;

	private final ListContext list;

	private final ObjectContext obj;

	private final String contextType;

	public EditorLoadContext(PCGenGraph pgg)
	{
		graph = new EditorGraphContext(pgg);
		obj = new EditorObjectContext();
		list = new EditorListContext();
		contextType = "Editor";
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
