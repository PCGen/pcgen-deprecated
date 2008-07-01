/*
 * AbilityChooserTab.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Jun 29, 2008, 10:30:57 PM
 */
package pcgen.gui.tabs;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import pcgen.gui.UIContext;
import pcgen.gui.facade.AbilityCatagoryFacade;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.tools.FilteredTreeViewDisplay;
import pcgen.gui.util.JTreeTable;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.DataViewColumn;
import pcgen.gui.util.treeview.DefaultDataViewColumn;
import pcgen.gui.util.treeview.TreeView;
import pcgen.gui.util.treeview.TreeViewPath;
import pcgen.gui.util.treeview.TreeViewTableModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class AbilityChooserTab extends ChooserTab
{

    private final FilteredTreeViewDisplay treeviewDisplay;
    private final JTreeTable catagoryTreeTable;

    public AbilityChooserTab(UIContext context)
    {
        super(context);
        this.treeviewDisplay = new FilteredTreeViewDisplay(context);
        this.catagoryTreeTable = new JTreeTable();
        initComponents();
    }

    private void initComponents()
    {
        JScrollPane catagoryScrollPane = new JScrollPane(catagoryTreeTable,
                                                         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setPrimaryChooserComponent(treeviewDisplay);
        setSecondaryChooserComponent(catagoryScrollPane);
    }

    @Override
    public Map<String, Object> saveModels()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadModels(Map<String, Object> map)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static final class AbilityCatagoryTreeViewTableModel extends TreeViewTableModel<AbilityCatagoryFacade>
    {

        private static final DataViewColumn column = new DefaultDataViewColumn("Remaining",
                                                                                  Integer.class);
        private static final TreeView<AbilityCatagoryFacade> treeview = new TreeView<AbilityCatagoryFacade>()
        {

            public String getViewName()
            {
                return "Catagory";
            }

            public List<TreeViewPath<AbilityCatagoryFacade>> getPaths(AbilityCatagoryFacade pobj)
            {
                TreeViewPath<AbilityCatagoryFacade> path = new TreeViewPath<AbilityCatagoryFacade>(pobj,
                                                                                                   pobj.getType());
                return Collections.singletonList(path);
            }

        };

        public AbilityCatagoryTreeViewTableModel(final CharacterFacade character,
                                                  List<AbilityCatagoryFacade> data)
        {
            super(new DataView<AbilityCatagoryFacade>()
          {

              public List<?> getData(AbilityCatagoryFacade catagory)
              {
                  return Collections.singletonList(character.getRemainingSelections(catagory));
              }

              public List<? extends DataViewColumn> getDataColumns()
              {
                  return Collections.singletonList(column);
              }

          });

        }

        @Override
        public boolean isCellEditable(Object node, int column)
        {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
            if (treeNode.getUserObject() instanceof AbilityCatagoryFacade)
            {
                return true;
            }
            return super.isCellEditable(node, column);
        }

    }
}
