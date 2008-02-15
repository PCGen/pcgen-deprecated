package pcgen.character.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.command.GraftFromNodeCommand;
import pcgen.base.graph.command.InsertEdgeCommand;
import pcgen.base.graph.visitor.EdgeTourist;
import pcgen.base.lang.Command;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.character.CharacterDataStore;

public class GrantToCharacter implements Command
{

	private final String name;
	private final PCGenGraph master;
	private final CharacterDataStore character;
	private final PrereqObject source;
	private final PrereqObject destination;

	public GrantToCharacter(String editName, PCGenGraph sourceG,
			CharacterDataStore dataStore, PrereqObject parent,
			PrereqObject child)
	{
		character = dataStore;
		master = sourceG;
		source = parent;
		destination = child;
		name = editName;
	}

	public UndoableEdit execute()
	{
		PCGenGraph graph = character.getBaseGraph();
		CompoundEdit edit = new CompoundEdit();
		GraftIntoCharacterDataStore graft = new GraftIntoCharacterDataStore(
				name, master, destination, graph);
		edit.addEdit(graft.execute());
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(source, destination,
				name);
		edit.addEdit(new InsertEdgeCommand<PrereqObject, PCGraphEdge>(name,
				character.getBaseGraph(), edge).execute());
		edit.end();
		return edit;
	}

	public String getPresentationName()
	{
		return name;
	}

	private class GraftIntoCharacterDataStore extends
			GraftFromNodeCommand<PrereqObject, PCGraphEdge>
	{

		public GraftIntoCharacterDataStore(String editName, PCGenGraph sourceG,
				PrereqObject gn, PCGenGraph destinationG)
		{
			super(editName, sourceG, gn, destinationG);
		}

		@Override
		public Collection<PCGraphEdge> getEdges(EdgeTourist<PCGraphEdge> dfta)
		{
			Set<PCGraphEdge> visitedEdges = dfta.getVisitedEdges();
			List<PCGraphEdge> list = new ArrayList<PCGraphEdge>(visitedEdges
					.size());
			for (PCGraphEdge edge : visitedEdges)
			{
				/*
				 * CONSIDER is there a way of limiting this to only edges that
				 * will have dynamic information?
				 */
				list.add(edge.createReplacementEdge(edge.getNodeAt(0), edge
						.getNodeAt(1)));
			}
			return visitedEdges;
		}

	}

}
