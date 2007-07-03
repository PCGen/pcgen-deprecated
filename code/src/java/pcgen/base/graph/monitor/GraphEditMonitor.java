/*
 * Copyright (c) Thomas Parker, 2004-2007.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * Created on Sep 10, 2004
 */
package pcgen.base.graph.monitor;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.EdgeChangeEvent;
import pcgen.base.graph.core.Graph;
import pcgen.base.graph.core.GraphChangeListener;
import pcgen.base.graph.core.NodeChangeEvent;
import pcgen.base.graph.edit.DeleteGraphEdge;
import pcgen.base.graph.edit.DeleteGraphNode;
import pcgen.base.graph.edit.InsertGraphEdge;
import pcgen.base.graph.edit.InsertGraphNode;
import pcgen.base.lang.UnreachableError;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A GraphEditMonitor is a GraphChangeListener that keeps track of changes to a
 * Graph. This includes objects which are added to and objects which are removed
 * from the Graph. If an object is added to a graph and subsequently removed
 * from the Graph, then it is also deleted from the sets maintained by the
 * GraphEditMonitor. A GraphEditMonitor also tracks the removal of objects which
 * were added to the graph before the GraphEditMonitor was added as a
 * GraphChangeListener to the Graph.
 * 
 * A GraphEditMonitor is especially useful for tracking side-effects to atomic
 * changes to a Graph. For example, a GraphEditMonitor can be added as a
 * GraphChangeListener to a Graph and then a Node can be deleted from the Graph.
 * All of the changes, which may include the removal of other Nodes or Edges,
 * will be captured by the GraphEditMonitor. Then a compound UndoableEdit which
 * reflects all of the changes to the Graph can be created by the
 * GraphEditMonitor. This drastically simplifies tracking changes to a Graph
 * (which is required in order to have UndoableEdit objects available).
 */
public final class GraphEditMonitor<N, ET extends Edge<N>> implements
		GraphChangeListener<N, ET>
{

	private static final String GRAPH_DOES_NOT_MATCH =
			"Graph does not match EditMonitor Graph";

	/**
	 * A constant indicating an edge was added to a graph
	 */
	private static final int AE = 0;

	/**
	 * An Integer representation of the constant for adding an edge to a graph
	 * (used for mapping to a Collection)
	 */
	private static final Integer ADD_EDGE = Integer.valueOf(AE);

	/**
	 * A constant indicating an edge was removed from a graph
	 */
	private static final int RE = 1;

	/**
	 * An Integer representation of the constant for removing an edge from a
	 * graph (used for mapping to a Collection)
	 */
	private static final Integer REMOVE_EDGE = Integer.valueOf(RE);

	/**
	 * A constant indicating a node was added to a graph
	 */
	private static final int AN = 2;

	/**
	 * An Integer representation of the constant for adding a node to a graph
	 * (used for mapping to a Collection)
	 */
	private static final Integer ADD_NODE = Integer.valueOf(AN);

	/**
	 * A constant indicating a node was removed from a graph
	 */
	private static final int RN = 3;

	/**
	 * An Integer representation of the constant for removing a node from a
	 * graph (used for mapping to a Collection)
	 */
	private static final Integer REMOVE_NODE = Integer.valueOf(RN);

	/**
	 * The Map of the objects added to or removed from the Graph -to-> the
	 * action that was taken on the object (addition, removal)
	 */
	private Map<Object, Integer> changes = new LinkedHashMap<Object, Integer>();

	/**
	 * The Graph which this GraphEditMonitor is monitoring.
	 * 
	 * FUTURE Abstract this away... is it necessary to capture the Graph as well
	 * as the Element that was changed? Is the most appropriate method to
	 * capture that a 2-key Map? (Removing this would allow this EditMonitor to
	 * monitor more than one Graph at a time, legally). Not sure if that's
	 * actually a GOOD thing, though :)
	 */
	private final Graph<N, ET> graph;

	/**
	 * Creates a new GraphEditMonitor to monitor the given Graph. Note that this
	 * does NOT actually register the GraphEditMontior as a GraphChangeListener
	 * of the given Graph, because it is NOT safe to release a reference to an
	 * object in the constructor. To construct a GraphEditMonitor and attach it
	 * to the underlying Graph see the getGraphEditMonitor() static method.
	 * 
	 * @param g
	 *            The Graph this GraphEditMonitor will monitor.
	 */
	private GraphEditMonitor(Graph<N, ET> g)
	{
		super();
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		graph = g;
	}

	/**
	 * Called when a Node is added to a Graph to which this GraphEditMonitor is
	 * listening.
	 * 
	 * @see pcgen.base.graph.core.GraphChangeListener#nodeAdded(pcgen.base.graph.core.NodeChangeEvent)
	 */
	public void nodeAdded(NodeChangeEvent<N> gce)
	{
		// Yes, this should be instance equality
		if (graph != gce.getSource())
		{
			throw new GraphMismatchException(GRAPH_DOES_NOT_MATCH);
		}
		N node = gce.getGraphNode();
		if (changes.containsKey(node))
		{
			if (changes.get(node).equals(REMOVE_NODE))
			{
				changes.remove(node);
			}
		}
		else
		{
			changes.put(node, ADD_NODE);
		}
	}

	/**
	 * Called when a Node is removed from a Graph to which this GraphEditMonitor
	 * is listening.
	 * 
	 * @see pcgen.base.graph.core.GraphChangeListener#nodeRemoved(pcgen.base.graph.core.NodeChangeEvent)
	 */
	public void nodeRemoved(NodeChangeEvent<N> gce)
	{
		// Yes, this should be instance equality
		if (graph != gce.getSource())
		{
			throw new GraphMismatchException(GRAPH_DOES_NOT_MATCH);
		}
		N node = gce.getGraphNode();
		if (changes.containsKey(node))
		{
			if (changes.get(node).equals(ADD_NODE))
			{
				changes.remove(node);
			}
		}
		else
		{
			changes.put(node, REMOVE_NODE);
		}
	}

	/**
	 * Called when an Edge is added to a Graph to which this GraphEditMonitor is
	 * listening.
	 * 
	 * @see pcgen.base.graph.core.GraphChangeListener#nodeAdded(pcgen.base.graph.core.NodeChangeEvent)
	 */
	public void edgeAdded(EdgeChangeEvent<N, ET> gce)
	{
		// Yes, this should be instance equality
		if (graph != gce.getSource())
		{
			throw new GraphMismatchException(GRAPH_DOES_NOT_MATCH);
		}
		Edge<N> edge = gce.getGraphEdge();
		if (changes.containsKey(edge))
		{
			if (changes.get(edge).equals(REMOVE_EDGE))
			{
				changes.remove(edge);
			}
		}
		else
		{
			changes.put(edge, ADD_EDGE);
		}
	}

	/**
	 * Called when an Edge is removed from a Graph to which this
	 * GraphEditMonitor is listening.
	 * 
	 * @see pcgen.base.graph.core.GraphChangeListener#nodeAdded(pcgen.base.graph.core.NodeChangeEvent)
	 */
	public void edgeRemoved(EdgeChangeEvent<N, ET> gce)
	{
		// Yes, this should be instance equality
		if (graph != gce.getSource())
		{
			throw new GraphMismatchException(GRAPH_DOES_NOT_MATCH);
		}
		Edge<N> edge = gce.getGraphEdge();
		if (changes.containsKey(edge))
		{
			if (changes.get(edge).equals(ADD_EDGE))
			{
				changes.remove(edge);
			}
		}
		else
		{
			changes.put(edge, REMOVE_EDGE);
		}
	}

	/**
	 * Returns an UndoableEdit containing the changes this GraphEditMonitor has
	 * witnessed on the underlying Graph. The UndoableEdit will have the given
	 * String as the Presentation Name of the UndoableEdit if the given String
	 * is non-null and is not solely composed of whitespace.
	 * 
	 * @param name
	 *            The presentation name for the UndoableEdit to be returned
	 * @return An UndoableEdit containing the changes this GraphEditMonitor has
	 *         witnessed on the underlying Graph
	 */
	public UndoableEdit getEdit(String name)
	{
		String editName;
		if (name == null || name.trim().length() == 0)
		{
			editName = "Monitored Edit on Graph";
		}
		else
		{
			editName = name;
		}
		if (changes.size() == 0)
		{
			return new AbstractUndoableEdit();
		}
		else if (changes.size() == 1)
		{
			return getEditFor(changes.keySet().iterator().next(), editName);
		}
		else
		{
			CompoundEdit e = new CompoundEdit();
			for (Object key : changes.keySet())
			{
				e.addEdit(getEditFor(key, editName));
			}
			e.end();
			return e;
		}
	}

	/**
	 * Constructs an Atomic Edit based on the given key.
	 * 
	 * @param key
	 *            The key used to get the change information from the changes
	 *            Map.
	 * 
	 * @param presentationName
	 *            The presentation name to be used for the Edit.
	 * @return An UndoableEdit representing the Atomic Edit stored in the
	 *         changes map for the given key.
	 */
	private UndoableEdit getEditFor(Object key, String presentationName)
	{
		UndoableEdit edit;
		switch (changes.get(key).intValue())
		{
			case AN:
				edit = new InsertGraphNode<N>(graph, (N) key, presentationName);
				break;
			case RN:
				edit = new DeleteGraphNode<N>(graph, (N) key, presentationName);
				break;
			case AE:
				edit =
						new InsertGraphEdge<N, ET>(graph, (ET) key,
							presentationName);
				break;
			case RE:
				edit =
						new DeleteGraphEdge<N, ET>(graph, (ET) key,
							presentationName);
				break;
			default:
				// impossible to get here
				// (would throw NPE first from the intValue() call above)
				throw new UnreachableError();
		}
		return edit;
	}

	/**
	 * Clears the list of changes in this GraphEditMonitor.
	 */
	public void clear()
	{
		changes = new LinkedHashMap<Object, Integer>();
	}

	/**
	 * Constructs a GraphEditMonitor for the given Graph AND attaches the
	 * GraphEditMonitor as a GraphChangeListener for the Graph.
	 * 
	 * @param g
	 *            The Graph the GraphEditMonitor should monitor for changes
	 * @return The GraphEditMonitor which is monitoring the given Graph for
	 *         changes
	 */
	public static <GT, ETN extends Edge<GT>> GraphEditMonitor<GT, ETN> getGraphEditMonitor(
		Graph<GT, ETN> g)
	{
		GraphEditMonitor<GT, ETN> edit = new GraphEditMonitor<GT, ETN>(g);
		g.addGraphChangeListener(edit);
		return edit;
	}

	/*
	 * FUTURE Do I need/want the ability to have two GraphEditMontiors to do a
	 * "diff"? Such that with GraphEditMonitor A started early and B started
	 * later, that I could do: GraphEditMonitor c = a.subtractEdits(b); and get
	 * GraphEditMonitor C which is only the edits in A which are not present in
	 * B? This allows for an efficient recovery system where there is a master
	 * GEM for generation which can take out the local GEM which is monitoring
	 * the local change (e.g. remove rediculous paths) and allow a partial
	 * generation to be left around.
	 */
}