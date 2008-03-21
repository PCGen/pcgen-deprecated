package pcgen.gui.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMPCClassLevel;
import pcgen.core.PObject;

public class GraphTreeModelFacade implements TreeModel {

	private final PCGenGraph graph;

	private Reference root;

	private List<PrereqObject> rootChildren;

	private final Class parentClass;
	
	public GraphTreeModelFacade(PCGenGraph pgg, Class cl) {
		graph = pgg;
		parentClass = cl;
		root = new RootReference(cl);
		rootChildren = new ArrayList<PrereqObject>();
		List<PrereqObject> list = graph.getNodeList();
		for (PrereqObject pro : list) {
			if (parentClass.equals(pro.getClass())) {
				rootChildren.add(pro);
			}
		}
	}

	public class Reference {
		public final PrereqObject ref;

		public Reference(PrereqObject pro) {
			ref = pro;
		}

		public String toString() {
			return ref.getClass().getSimpleName() + " " + ref.toString()
					+ getSupplement();
		}

		public String getSupplement() {
			if (ref instanceof PObject) {
				PObject po = (PObject) ref;
				if (!po.getDisplayName().equals(po.getKeyName())) {
					return " (" + po.getKeyName() + ")";
				}
			}
			return "";
		}
	}

	public class SomethingReference extends Reference {

		public SomethingReference(PrereqObject pro) {
			super(pro);
		}

		public String toString() {
			return "WEIRD Relationship: " + super.toString();
		}

	}

	public class RootReference extends Reference {

		private final Class clazz;
		
		public RootReference(Class cl) {
			super(null);
			clazz = cl;
		}

		public String toString() {
			return "All " + clazz.getSimpleName() + " PObjects";
		}

	}

	public class PrimaryReference extends Reference {

		public PrimaryReference(PrereqObject pro) {
			super(pro);
		}

	}

	public void addTreeModelListener(TreeModelListener arg0) {
		// FIXME Auto-generated method stub

	}

	private Map<String, PrereqObject> map = new HashMap<String, PrereqObject>();
	
	public Object getChild(Object arg0, int arg1) {
		Reference r = getChildThingy(arg0, arg1);
		map.put(r.toString(), r.ref);
		return r;
	}

	private Reference getChildThingy(Object arg0, int arg1) {
		if (arg0.equals(root)) {
			return new PrimaryReference(rootChildren.get(arg1));
		} else {
			List<PCGraphEdge> outwardEdgeList = graph
					.getOutwardEdgeList(((Reference) arg0).ref);
			if (((Reference) arg0).ref instanceof CDOMPCClass) {
				if (arg1 >= outwardEdgeList.size()) {
					Collection<CDOMPCClassLevel> classLevelCollection = ((CDOMPCClass) ((Reference) arg0).ref)
							.getClassLevelCollection();
					return new Reference(new ArrayList<CDOMPCClassLevel>(
							classLevelCollection).get(arg1
							- outwardEdgeList.size()));
				}
			}
			PCGraphEdge edge = outwardEdgeList.get(arg1);
			if (edge instanceof PCGraphGrantsEdge) {
				return new Reference(edge.getSinkNodes().get(0));
			} else {
				return new SomethingReference(edge.getSinkNodes().get(0));
			}
		}
	}

	public int getChildCount(Object arg0) {
		if (arg0.equals(root)) {
			return rootChildren.size();
		}
		PrereqObject pro = ((Reference) arg0).ref;
		if (!(arg0 instanceof PrimaryReference)
				&& parentClass.equals(pro.getClass())) {
			return 0;
		}
		int adder = 0;
		if (pro instanceof CDOMPCClass) {
			adder = ((CDOMPCClass) pro).getClassLevelCount();
		}
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(pro);
		return adder + (outwardEdgeList == null ? 0 : outwardEdgeList.size());
	}

	public int getIndexOfChild(Object arg0, Object arg1) {
		if (arg0.equals(root)) {
			return rootChildren.indexOf(arg1);
		}
		if (!(arg0 instanceof PrimaryReference)
				&& parentClass.equals(((Reference) arg0).ref.getClass())) {
			return -1;
		}
		List<PCGraphEdge> list = graph
				.getOutwardEdgeList(((Reference) arg0).ref);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getSinkNodes().get(0)
					.equals(((Reference) arg1).ref)) {
				return i;
			}
		}
		if (((Reference) arg0).ref instanceof CDOMPCClass) {
			int adder = list.size();
			ArrayList<CDOMPCClassLevel> cl = new ArrayList<CDOMPCClassLevel>(
					((CDOMPCClass) ((Reference) arg0).ref)
							.getClassLevelCollection());
			for (int i = 0; i < cl.size(); i++) {
				if (cl.get(i).equals(((Reference) arg1).ref)) {
					return i + adder;
				}
			}
		}

		return -1;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object arg0) {
		return getChildCount(arg0) == 0;
	}

	public void removeTreeModelListener(TreeModelListener arg0) {
		// FIXME Auto-generated method stub

	}

	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// FIXME Auto-generated method stub

	}

	public PrereqObject getPrereqObject(String str) {
		return map.get(str);
	}

}
