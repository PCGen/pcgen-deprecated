package pcgen.cdom.factory;

import pcgen.cdom.base.CDOMEdgeReference;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.PlayerCharacter;

public class AllowFactory<T extends CDOMObject> extends AbstractFactory<T>
{

	private final CDOMList<T> list;

	public AllowFactory(PCGraphGrantsEdge edge,
		CDOMList<T> cdomList)
	{
		super(edge);
		list = cdomList;
	}

	public AllowFactory(CDOMEdgeReference ref,
		CDOMList<T> cdomList)
	{
		super(ref);
		list = cdomList;
	}

	@Override
	public void execute(PlayerCharacter pc)
	{
		/*
		 * TODO "Allow" the objects associated to the Choice object that is the
		 * child of the edge e. These objects are granted to the PC's list, as
		 * children of this GrantFactory, if they are not already granted as
		 * children of this factory...
		 */
		/*
		 * TODO Make sure that the assocations in assoc are placed into the
		 * list...
		 */
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof AllowFactory)
		{
			return equalsAbstractFactory((AbstractFactory<?>) o);
		}
		return false;
	}
}
