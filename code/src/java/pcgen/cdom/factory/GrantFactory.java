package pcgen.cdom.factory;

import pcgen.cdom.base.CDOMEdgeReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.PlayerCharacter;

public class GrantFactory<T extends PrereqObject> extends AbstractFactory<T>
{
	public GrantFactory(PCGraphGrantsEdge edge)
	{
		super(edge);
	}

	public GrantFactory(CDOMEdgeReference ref)
	{
		super(ref);
	}

	@Override
	public void execute(PlayerCharacter pc)
	{
		/*
		 * TODO Grant the objects associated to the Choice object that is the
		 * child of the edge e. These objects are granted to the PC, as children
		 * of this GrantFactory, if they are not already granted as children of
		 * this factory...
		 */
		/*
		 * TODO Make sure that the assocations in assoc are placed into the
		 * edge...
		 */
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof GrantFactory)
		{
			return equalsAbstractFactory((AbstractFactory<?>) o);
		}
		return false;
	}
}
