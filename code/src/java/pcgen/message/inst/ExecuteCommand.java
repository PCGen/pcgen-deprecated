package pcgen.message.inst;

import pcgen.base.lang.Command;
import pcgen.message.base.PCGenMessage;

public class ExecuteCommand implements PCGenMessage
{

	private final Command command;

	public ExecuteCommand(Command c)
	{
		command = c;
	}

	public Command getCommand()
	{
		return command;
	}
}
