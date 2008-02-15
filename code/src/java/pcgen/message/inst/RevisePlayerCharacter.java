package pcgen.message.inst;

import pcgen.core.PlayerCharacter;
import pcgen.message.base.PCGenMessage;

public class RevisePlayerCharacter implements PCGenMessage
{

	private final PlayerCharacter character;
	private final int revision;

	public RevisePlayerCharacter(PlayerCharacter pc, int rev)
	{
		character = pc;
		revision = rev;
	}

	public PlayerCharacter getCharacter()
	{
		return character;
	}

	public int getRevision()
	{
		return revision;
	}

}
