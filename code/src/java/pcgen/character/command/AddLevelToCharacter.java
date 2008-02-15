package pcgen.character.command;

import javax.swing.undo.UndoableEdit;

import pcgen.base.lang.Command;
import pcgen.character.CharacterFacade;
import pcgen.core.PCClass;

public class AddLevelToCharacter implements Command
{

	private final String name;
	private final CharacterFacade character;
	private final PCClass cl;

	public AddLevelToCharacter(String editName, CharacterFacade dataStore,
			PCClass addedClass)
	{
		character = dataStore;
		cl = addedClass;
		name = editName;
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
