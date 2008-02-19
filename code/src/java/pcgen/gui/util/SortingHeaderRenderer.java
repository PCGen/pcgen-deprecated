/*
 * SortingHeaderRenderer.java
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
 * Created on Feb 18, 2008, 2:09:04 PM
 */
package pcgen.gui.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import pcgen.gui.proto.util.JTreeViewTableHeader;
import pcgen.gui.util.treeview.DataViewColumn;
import pcgen.gui.util.treeview.TreeViewTableModel;
import pcgen.util.Comparators;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class SortingHeaderRenderer extends JButton implements TableCellRenderer,
                                                               SortingConstants
{

    private static final ButtonModel defaultModel = new DefaultButtonModel();
    private final ButtonModel usedModel = new DefaultButtonModel();
    private final JTreeViewTableHeader header;
    private Map<TableColumn, Icon> iconMap = Collections.emptyMap();
    private TableColumn trackedColumn = null;

    public SortingHeaderRenderer(final JTreeViewTableHeader header)
    {

        this.header = header;
        header.addMouseListener(
                new MouseListener()
                {

                    public void mouseClicked(MouseEvent e)
                    {
                        doClick();
                    }

                    public void mousePressed(MouseEvent e)
                    {
                        usedModel.setPressed(true);
                        header.repaint();
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

                });
        header.addMouseMotionListener(
                new MouseMotionListener()
                {

                    public void mouseDragged(MouseEvent e)
                    {

                    }

                    public void mouseMoved(MouseEvent e)
                    {
                        TableColumnModel model = header.getColumnModel();
                        trackedColumn = model.getColumn(model.getColumnIndexAtX(e.getX()));
                    }

                });
        addActionListener(
                new ActionListener()
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        TreeViewTableModel model = header.getTableModel();
                        Icon icon = null;
                        if (iconMap.containsKey(trackedColumn))
                        {
                            icon = DESCENDING_ICON;
                        }
                        else
                        {
                            icon = ASCENDING_ICON;
                        }
                        iconMap = Collections.singletonMap(trackedColumn,
                                                           icon);
                        model.sortColumn(trackedColumn.getModelIndex());
                    }

                });
//        this.setRolloverEnabled(true);
        setHorizontalTextPosition(LEADING);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                    boolean isSelected,
                                                    boolean hasFocus, int row,
                                                    int column)
    {
        if (trackedColumn != null && trackedColumn.getHeaderValue() == value &&
                trackedColumn == header.getDraggedColumn())
        {
            setModel(usedModel);
        }
        else
        {
            setModel(defaultModel);
        }
        setIcon(iconMap.get(trackedColumn));
        setText(value.toString());
        return this;
    }

}
