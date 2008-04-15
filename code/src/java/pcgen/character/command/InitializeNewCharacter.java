package pcgen.character.command;

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.monitor.GraphEditMonitor;
import pcgen.base.lang.Command;
import pcgen.cdom.actor.GrantActor;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.choiceset.AnyChoiceSet;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.inst.CDOMAlignment;
import pcgen.cdom.inst.CDOMRace;
import pcgen.character.CharacterDataStore;

public class InitializeNewCharacter implements Command
{

	private final String name;
	private final CharacterDataStore character;

	public InitializeNewCharacter(String commandName,
			CharacterDataStore dataStore)
	{
		name = commandName;
		character = dataStore;
	}

	public UndoableEdit execute()
	{
		PCGenGraph graph = character.getBaseGraph();
		/*
		 * Note that this method of execution (passively collecting concurrent
		 * changes) is NOT thread safe.
		 */
		GraphEditMonitor<PrereqObject, PCGraphEdge> edit = GraphEditMonitor
				.getGraphEditMonitor(graph);

		ChooseActionContainer alignment = getSingleShot("ALIGNMENT",
				CDOMAlignment.class);
		graph.addNode(alignment);
		graph.addEdge(new PCGraphGrantsEdge(graph.getRoot(), alignment,
				"*Initialization"));

		ChooseActionContainer race = getSingleShot("RACE", CDOMRace.class);
		graph.addNode(race);
		graph.addEdge(new PCGraphGrantsEdge(graph.getRoot(), race,
				"*Initialization"));

		graph.removeGraphChangeListener(edit);
		return edit.getEdit(name);
	}

	private <T extends CDOMObject> ChooseActionContainer getSingleShot(
			String align, Class<T> alignClass)
	{
		ChooseActionContainer container = new ChooseActionContainer(align);
		container.addActor(new GrantActor<CDOMAlignment>());
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
				.getFormulaFor(1));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
				.getFormulaFor(1));
		AnyChoiceSet<T> acs = new AnyChoiceSet<T>(alignClass);
		container.setChoiceSet(new ChoiceSet<T>(align, acs));
		return container;
	}

	public String getPresentationName()
	{
		return name;
	}

}
