package pcgen.cdom.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.graph.visitor.DirectedBreadthFirstTraverseAlgorithm;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.EquipmentSet;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Equipment;

public class EquipmentSetFacade
{

	private final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;

	private final PCGenGraph graph;

	private final EquipmentSet set;

	public EquipmentSetFacade(PCGenGraph pcgg, EquipmentSet activeSet)
	{
		if (pcgg == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		if (activeSet == null)
		{
			throw new IllegalArgumentException(
				"Active EquipmentSet cannot be null");
		}
		graph = pcgg;
		set = activeSet;
	}

	public Collection<Equipment> getEquipment()
	{
		DirectedBreadthFirstTraverseAlgorithm<PrereqObject, PCGraphEdge> trav =
				new DirectedBreadthFirstTraverseAlgorithm<PrereqObject, PCGraphEdge>(
					graph);
		trav.traverseFromNode(set);
		List<Equipment> list = new ArrayList<Equipment>();
		for (PrereqObject po : trav.getVisitedNodes())
		{
			if (EQUIPMENT_CLASS.isInstance(po))
			{
				list.add(EQUIPMENT_CLASS.cast(po));
			}
		}
		return list;
	}

	public Collection<Equipment> getEquipment(final int location)
	{
		DirectedBreadthFirstTraverseAlgorithm<PrereqObject, PCGraphEdge> trav =
				new DirectedBreadthFirstTraverseAlgorithm<PrereqObject, PCGraphEdge>(
					graph)
				{

					@Override
					protected boolean canTraverseEdge(PCGraphEdge edge,
						PrereqObject gn, int type)
					{
						if (super.canTraverseEdge(edge, gn, type))
						{
							Integer loc =
									edge
										.getAssociation(AssociationKey.EQUIPMENT_LOCATION);
							if (loc != null)
							{
								return loc.intValue() == location;
							}
						}
						return false;
					}

				};
		trav.traverseFromNode(set);
		List<Equipment> list = new ArrayList<Equipment>();
		for (PrereqObject po : trav.getVisitedNodes())
		{
			if (EQUIPMENT_CLASS.isInstance(po))
			{
				list.add(EQUIPMENT_CLASS.cast(po));
			}
		}
		return list;
	}
}
