package pcgen.message.inst;

import pcgen.core.facade.PlayerCharacterFacade;
import pcgen.message.base.PCGenMessage;

public class RevisePlayerCharacter implements PCGenMessage
{

	private final PlayerCharacterFacade character;
	private final int revision;

	public RevisePlayerCharacter(PlayerCharacterFacade pc, int rev)
	{
		character = pc;
		revision = rev;
	}

	public PlayerCharacterFacade getCharacter()
	{
		return character;
	}

	public int getRevision()
	{
		return revision;
	}

}
