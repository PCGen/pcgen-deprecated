/*
 * JTreeViewTableHeader.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on Feb 16, 2008, 8:27:21 PM
 */
package pcgen.gui.proto.util;

import pcgen.gui.util.JTreeViewTable;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.table.TableColumn;
import pcgen.gui.util.SortingHeaderRenderer;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import pcgen.gui.util.treeview.TreeViewTableModel;

/**
 *
 * @author Connor Petty<mistercpp2000@gmail.com>
 */
public class JTreeViewTableHeader extends JTableHeader
{

    private TreeViewTableModel tableModel;
    private TableColumn trackedColumn;

    public JTreeViewTableHeader(JTreeViewTable table)
    {
        super(table.getColumnModel());
        addMouseMotionListener(new ColumnTracker());
        tableModel = table.getTreeViewTableModel();
    }

    @Override
    public TableCellRenderer createDefaultRenderer()
    {
        return new CompoundHeaderRenderer();
    }

    public TableColumn getTrackedColumn()
    {
        return trackedColumn;
    }

    public TreeViewTableModel getTableModel()
    {
        return tableModel;
    }

    private final class ColumnTracker implements MouseMotionListener
    {

        public void mouseDragged(MouseEvent e)
        {

        }

        public void mouseMoved(MouseEvent e)
        {
            TableColumnModel model = getColumnModel();
            trackedColumn = model.getColumn(model.getColumnIndexAtX(e.getX()));
        }

    }

    private final class CompoundHeaderRenderer implements TableCellRenderer
    {

        private TreeViewHeaderRenderer treeRenderer = new TreeViewHeaderRenderer(JTreeViewTableHeader.this);
        private SortingHeaderRenderer sortRenderer = new SortingHeaderRenderer(JTreeViewTableHeader.this);

        public Component getTableCellRendererComponent(JTable table,
                                                        Object value,
                                                        boolean isSelected,
                                                        boolean hasFocus,
                                                        int row,
                                                        int column)
        {
            if (value == null)
            {
                return treeRenderer.getTableCellRendererComponent(table, value,
                                                                  isSelected,
                                                                  hasFocus, row,
                                                                  column);
            }
            else
            {
                return sortRenderer.getTableCellRendererComponent(table, value,
                                                                  isSelected,
                                                                  hasFocus, row,
                                                                  column);
            }
        }

    }
}
