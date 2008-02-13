package pcgen.cdom.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import pcgen.base.graph.command.GraftFromNodeCommand;
import pcgen.base.graph.visitor.EdgeTourist;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;

public class GraftIntoPlayerCharacter extends
		GraftFromNodeCommand<PrereqObject, PCGraphEdge>
{

	public GraftIntoPlayerCharacter(String editName, PCGenGraph sourceG,
			PrereqObject gn, PCGenGraph destinationG)
	{
		super(editName, sourceG, gn, destinationG);
	}

	@Override
	public Collection<PCGraphEdge> getEdges(EdgeTourist<PCGraphEdge> dfta)
	{
		Set<PCGraphEdge> visitedEdges = dfta.getVisitedEdges();
		List<PCGraphEdge> list = new ArrayList<PCGraphEdge>(visitedEdges.size());
		for (PCGraphEdge edge : visitedEdges)
		{
			/*
			 * CONSIDER is there a way of limiting this to only edges that will
			 * have dynamic information?
			 */
			list.add(edge.createReplacementEdge(edge.getNodeAt(0), edge
					.getNodeAt(1)));
		}
		return visitedEdges;
	}

}
