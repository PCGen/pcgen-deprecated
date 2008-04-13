package pcgen.rules.context;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.Command;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrereqObject;

public class ContextQueue
{

	private final GraphContext graph;

	public ContextQueue(GraphContext gc)
	{
		if (gc == null)
		{
			throw new IllegalArgumentException();
		}
		graph = gc;
	}

	private final List<Command> commands = new ArrayList<Command>();

	public void commit()
	{
		for (Command c : commands)
		{
			c.execute();
		}
	}

	public AssociatedPrereqObject grant(String sourceToken, CDOMObject obj,
		PrereqObject pro)
	{
		return null;//TODO return graph.grant(sourceToken, obj, pro);
	}

}
