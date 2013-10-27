package pcgen.gui.graph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.core.Ability;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.spell.Spell;

public class GraphViewFrame extends JFrame {

	private List<Class<? extends PObject>> classList = Arrays.asList(
			Race.class, PCClass.class, Spell.class, Skill.class, Deity.class,
			Domain.class, Ability.class, Equipment.class,
			EquipmentModifier.class);

	private Map<String, GraphTreeModelFacade> map = new HashMap<String, GraphTreeModelFacade>();
	
	private GraphTreeModelFacade activeGTMF = null;

	public GraphViewFrame(PCGenGraph master) {
		JTree topTree = new JTree(classList.toArray());
		topTree.setEditable(false);
		DefaultTreeSelectionModel sm = new DefaultTreeSelectionModel();
		sm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		topTree.setSelectionModel(sm);
		setLayout(new BorderLayout());
		add(new JScrollPane(topTree), BorderLayout.WEST);

		final JPanel detail = new JPanel();
		detail.setLayout(new GridLayout(0, 1));
		detail.setPreferredSize(new Dimension(500, 500));
		add(new JScrollPane(detail), BorderLayout.EAST);

		for (Class clazz : classList) {
			final GraphTreeModelFacade gtmf = new GraphTreeModelFacade(master,
					clazz);
			map.put(clazz.toString(), gtmf);
		}
		final JTree tree = new JTree();
		TreeSelectionListener treeL = new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				TreePath tp = arg0.getNewLeadSelectionPath();
				String str = tp.getLastPathComponent().toString();
				PrereqObject pro = activeGTMF.getPrereqObject(str);
				detail.removeAll();
				if (pro instanceof CDOMObject) {
					addDetail(detail, (CDOMObject) pro);
				}
				detail.invalidate();
				GraphViewFrame.this.validateTree();
				GraphViewFrame.this.repaint();
			}
		};
		tree.addTreeSelectionListener(treeL);
		JScrollPane jsp = new JScrollPane(tree);
		add(jsp, BorderLayout.CENTER);

		TreeSelectionListener tsl = new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				TreePath tp = arg0.getNewLeadSelectionPath();
				String str = tp.getLastPathComponent().toString();
				activeGTMF = map.get(str);
				tree.setModel(activeGTMF);
				tree.invalidate();
				GraphViewFrame.this.validateTree();
				GraphViewFrame.this.repaint();
			}
		};
		topTree.addTreeSelectionListener(tsl);
		topTree.addSelectionRow(0);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void addDetail(JPanel detail, CDOMObject object) {
		detail.add(new JLabel("Name: " + object.getDisplayName()));
		detail.add(new JLabel("Key Name: " + object.getKeyName()));
		detail.add(new JLabel("Source: " + object.getSourceEntry()));
		for (IntegerKey ik : IntegerKey.values()) {
			if (object.containsKey(ik)) {
				detail.add(new JLabel(ik + ": " + object.get(ik)));
			}
		}
		for (StringKey sk : StringKey.values()) {
			if (object.containsKey(sk)) {
				detail.add(new JLabel(sk + ": " + object.get(sk)));
			}
		}
		for (FormulaKey fk : FormulaKey.getAllConstants()) {
			if (object.containsKey(fk)) {
				detail.add(new JLabel(fk + ": " + object.get(fk)));
			}
		}
		for (ObjectKey<?> ok : ObjectKey.getAllConstants()) {
			if (object.containsKey(ok)) {
				detail.add(new JLabel(ok + ": " + object.get(ok)));
			}
		}
		for (ListKey<?> lk : ListKey.getAllConstants()) {
			if (object.containsListFor(lk)) {
				detail.add(new JLabel(lk + ": " + object.getListFor(lk)));
			}
		}
		// for (MapKey mk : MapKey.getAllConstants()) {
		// if (object.containsKey(mk)) {
		// detail.add(new JTextField(mk + ": " + object.get(mk)));
		// }
		// }
	}
}
