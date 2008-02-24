/*
 * JTableSortingHeader.java
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
package pcgen.gui.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import pcgen.gui.util.ModelSorter.SortingPriority;

/**
 *
 * @author Connor Petty<mistercpp2000@gmail.com>
 */
public class JTableSortingHeader extends JTableHeader
{

    private static final Icon ASCENDING_ICON = IconUtilities.getImageIcon("Down16.gif");//TODO: implement
    private static final Icon DESCENDING_ICON = IconUtilities.getImageIcon("Up16.gif");
    private static final ButtonModel defaultModel = new DefaultButtonModel();
    private final ButtonModel usedModel = new DefaultButtonModel();
    private ModelSorter sorter;
    private TableColumn trackedColumn;

    public JTableSortingHeader(JTableEx table)
    {
        super(table.getColumnModel());
        MouseTracker tracker = new MouseTracker();
        addMouseListener(tracker);
        addMouseMotionListener(tracker);
        sorter = table.getModelSorter();
    }

    @Override
    protected TableCellRenderer createDefaultRenderer()
    {
        return new SortingHeaderRenderer();
    }

    public TableColumn getTrackedColumn()
    {
        return trackedColumn;
    }

    public ModelSorter getModelSorter()
    {
        return sorter;
    }

    private final class MouseTracker implements MouseMotionListener,
                                                  MouseListener
    {

        public void mouseClicked(MouseEvent e)
        {
            if (getCursor() == Cursor.getDefaultCursor())
            {
                sorter.toggleSort(trackedColumn.getModelIndex());
                repaint();
            }
        }

        public void mouseDragged(MouseEvent e)
        {

        }

        public void mouseMoved(MouseEvent e)
        {
            TableColumnModel model = getColumnModel();
            trackedColumn = model.getColumn(model.getColumnIndexAtX(e.getX()));
        }

        public void mousePressed(MouseEvent e)
        {
            usedModel.setPressed(true);
            repaint();
        }

        public void mouseReleased(MouseEvent e)
        {
            usedModel.setPressed(false);
        }

        public void mouseEntered(MouseEvent e)
        {
            usedModel.setRollover(true);
        }

        public void mouseExited(MouseEvent e)
        {
            usedModel.setRollover(false);
        }

    }

    public class SortingHeaderRenderer extends JButton implements TableCellRenderer
    {

        public SortingHeaderRenderer()
        {
            setHorizontalTextPosition(LEADING);
        }

        public Component getTableCellRendererComponent(JTable table,
                                                        Object value,
                                                        boolean isSelected,
                                                        boolean hasFocus,
                                                        int row,
                                                        int column)
        {
            if (trackedColumn != null && trackedColumn.getHeaderValue() == value &&
                    trackedColumn == getDraggedColumn())
            {
                setModel(usedModel);
            }
            else
            {
                setModel(defaultModel);
            }
            Icon icon = null;
            TableColumn currentColumn = table.getColumn(value);
            List<? extends SortingPriority> list = sorter.getSortingPriority();
            if (!list.isEmpty())
            {
                SortingPriority order = list.get(0);
                if (order.getColumn() == currentColumn.getModelIndex())
                {
                    switch (order.getMode())
                    {
                        case ASCENDING:
                            icon = ASCENDING_ICON;
                            break;
                        case DESCENDING:
                            icon = DESCENDING_ICON;
                            break;
                    }
                }
            }
            setIcon(icon);
            setText(value.toString());
            return this;
        }

    }
}
