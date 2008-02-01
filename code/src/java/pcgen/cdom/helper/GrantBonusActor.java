package pcgen.cdom.helper;

import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.inst.SimpleAssociatedObject;
import pcgen.core.PlayerCharacter;

public class GrantBonusActor extends SimpleAssociatedObject implements
		LSTWriteable, ChooseActor
{

	private final String bonus;

	public GrantBonusActor(String bonusString)
	{
		bonus = bonusString;
	}

	public void execute(PlayerCharacter pc)
	{
		/*
		 * TODO From the Association of the Choice Object (needs to be passed in
		 * to this method??), this should Grant additional "weight" to the
		 * masterReference object (an effective spell casting level)
		 */
		/*
		 * TODO Make sure that the assocations in assoc are placed into the
		 * edge...
		 */
	}

	public String getLSTformat()
	{
		return bonus;
	}

	@Override
	public int hashCode()
	{
		return bonus.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof GrantBonusActor
				&& bonus.equals(((GrantBonusActor) o).bonus);
	}
}
