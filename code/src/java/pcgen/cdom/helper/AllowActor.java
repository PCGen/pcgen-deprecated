package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.core.PlayerCharacter;

public class AllowActor<T extends CDOMObject> extends ConcretePrereqObject
		implements LSTWriteable, ChooseActor
{

	private final CDOMList<T> list;

	public AllowActor(CDOMList<T> cdomList)
	{
		list = cdomList;
	}

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

	public String getLSTformat()
	{
		return null;
	}

	@Override
	public int hashCode()
	{
		return -26;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof AllowActor;
	}
}
