package pcgen.cdom.actor;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.helper.ChooseActor;
import pcgen.core.PlayerCharacter;

public class RemoveActor extends ConcretePrereqObject implements LSTWriteable,
		ChooseActor
{
	public void execute(PlayerCharacter pc)
	{
		/*
		 * TODO Remove the objects associated to the Choice object that is the
		 * child of the edge e. These objects are removed from the PC, and their
		 * previous association must be stored by this object in some fashion...
		 */
	}

	public String getLSTformat()
	{
		return null;
	}

	@Override
	public int hashCode()
	{
		return -30;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof RemoveActor;
	}
}
