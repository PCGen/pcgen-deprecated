package pcgen.character.command;

import javax.swing.undo.UndoableEdit;

import pcgen.base.lang.Command;
import pcgen.cdom.base.PrereqObject;
import pcgen.character.CharacterFacade;

public class GrantToCharacter implements Command
{

	private final String name;
	private final CharacterFacade character;
	private final PrereqObject source;
	private final PrereqObject destination;

	public GrantToCharacter(String editName, 
			CharacterFacade dataStore, PrereqObject parent,
			PrereqObject child)
	{
		character = dataStore;
		source = parent;
		destination = child;
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
