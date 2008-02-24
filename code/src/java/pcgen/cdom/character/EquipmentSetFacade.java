package pcgen.cdom.character;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.graph.visitor.DirectedBreadthFirstTraverseAlgorithm;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.EquipmentSet;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.CDOMEquipment;

public class EquipmentSetFacade
{

	private final Class<CDOMEquipment> EQUIPMENT_CLASS = CDOMEquipment.class;

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

	public Collection<CDOMEquipment> getEquipment()
	{
		DirectedBreadthFirstTraverseAlgorithm<PrereqObject, PCGraphEdge> trav =
				new DirectedBreadthFirstTraverseAlgorithm<PrereqObject, PCGraphEdge>(
					graph);
		trav.traverseFromNode(set);
		List<CDOMEquipment> list = new ArrayList<CDOMEquipment>();
		for (PrereqObject po : trav.getVisitedNodes())
		{
			if (EQUIPMENT_CLASS.isInstance(po))
			{
				list.add(EQUIPMENT_CLASS.cast(po));
			}
		}
		return list;
	}

	public Collection<CDOMEquipment> getEquipment(final int location)
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
		List<CDOMEquipment> list = new ArrayList<CDOMEquipment>();
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
