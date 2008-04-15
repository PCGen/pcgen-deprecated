package pcgen.cdom.actor;

import pcgen.cdom.base.ChooseActor;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.core.PlayerCharacter;

public class GrantActor<T extends PrereqObject> extends SimpleAssociatedObject
		implements LSTWriteable, ChooseActor
{
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

	public String getLSTformat()
	{
		return null;
	}

	@Override
	public int hashCode()
	{
		return -22;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof GrantActor;
	}
}
