package pcgen.message.inst;

import pcgen.core.facade.PlayerCharacterFacade;
import pcgen.message.base.PCGenMessage;

public class SetActivePlayerCharacter implements PCGenMessage
{

	private final PlayerCharacterFacade character;
	
	public SetActivePlayerCharacter(PlayerCharacterFacade pc)
	{
		character = pc;
	}

	public PlayerCharacterFacade getCharacter()
	{
		return character;
	}
	
}
