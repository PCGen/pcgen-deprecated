package pcgen.cdom.factory;

import pcgen.cdom.base.CDOMEdgeReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.PlayerCharacter;

public class RemoveFactory<T extends PrereqObject> extends AbstractFactory<T>
{

	public RemoveFactory(PCGraphGrantsEdge edge)
	{
		super(edge);
	}

	public RemoveFactory(CDOMEdgeReference ref)
	{
		super(ref);
	}

	@Override
	public void execute(PlayerCharacter pc)
	{
		/*
		 * TODO Remove the objects associated to the Choice object that is the
		 * child of the edge e. These objects are removed from the PC, and their
		 * previous association must be stored by this object in some fashion...
		 */
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof RemoveFactory)
		{
			return equalsAbstractFactory((AbstractFactory<?>) o);
		}
		return false;
	}
}
