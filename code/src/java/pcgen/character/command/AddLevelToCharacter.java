package pcgen.character.command;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.lang.Command;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.character.CharacterDataStore;
import pcgen.core.PCCharacterLevel;
import pcgen.core.PCClass;
import pcgen.rules.RulesDataStore;

public class AddLevelToCharacter implements Command
{

	private final String name;
	private final RulesDataStore rules;
	private final CharacterDataStore character;
	private final PCClass cl;

	public AddLevelToCharacter(String editName, RulesDataStore rulesData,
			CharacterDataStore dataStore, PCClass addedClass)
	{
		character = dataStore;
		rules = rulesData;
		cl = addedClass;
		name = editName;
	}

	public UndoableEdit execute()
	{
		PCGenGraph graph = character.getBaseGraph();
		PCCharacterLevel lvl = rules.getLevel(character.getLevel() + 1);
		PCClassLevel classLvl = cl.getClassLevel(character.getLevel(cl) + 1);
		/*
		 * Note that this method of execution (passively collecting concurrent
		 * changes) is NOT thread safe.
		 */
		CompoundEdit edit = new CompoundEdit();
		UndoableEdit levelEdit = new GrantToCharacter(name, graph, character,
				graph.getRoot(), lvl).execute();
		edit.addEdit(levelEdit);
		UndoableEdit classLevelEdit = new GrantToCharacter(name, graph,
				character, lvl, classLvl).execute();
		edit.addEdit(classLevelEdit);
		UndoableEdit classEdit = new GrantToCharacter(name, graph, character,
				classLvl, cl).execute();
		edit.addEdit(classEdit);
		edit.end();
		return edit;
	}

	public String getPresentationName()
	{
		return name;
	}
}
