package pcgen.message.inst;

import pcgen.core.PlayerCharacter;
import pcgen.message.base.PCGenMessage;

public class SetActivePlayerCharacter implements PCGenMessage
{

	private final PlayerCharacter character;
	
	public SetActivePlayerCharacter(PlayerCharacter pc)
	{
		character = pc;
	}

	public PlayerCharacter getCharacter()
	{
		return character;
	}
	
}
