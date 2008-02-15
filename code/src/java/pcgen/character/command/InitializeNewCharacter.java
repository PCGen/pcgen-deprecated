package pcgen.character.command;

import javax.swing.undo.UndoableEdit;

import pcgen.base.lang.Command;
import pcgen.character.CharacterFacade;

public class InitializeNewCharacter implements Command
{

	private final String name;
	private final CharacterFacade character;

	public InitializeNewCharacter(String commandName, CharacterFacade dataStore)
	{
		name = commandName;
		character = dataStore;
	}

	public UndoableEdit execute()
	{
		return null;
	}

	public String getPresentationName()
	{
		return name;
	}

}
