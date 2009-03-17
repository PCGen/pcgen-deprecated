package pcgen.character.command;

import javax.swing.undo.UndoableEdit;

import pcgen.base.lang.Command;
import pcgen.character.CharacterFacade;
import pcgen.core.facade.PCClassFacade;

public class AddLevelToCharacter implements Command
{

	private final String name;
	private final CharacterFacade character;
	private final PCClassFacade cl;

	public AddLevelToCharacter(String editName, CharacterFacade dataStore,
			PCClassFacade addedClass)
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
