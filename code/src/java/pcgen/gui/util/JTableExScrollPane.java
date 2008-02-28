/*
 * JTableExScrollPane.java
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
 * Created on Feb 27, 2008, 10:44:14 PM
 */
package pcgen.gui.util;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import pcgen.gui.util.table.DynamicTableColumnModel;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class JTableExScrollPane extends JScrollPane
{

    private JTableEx table;

    public JTableExScrollPane()
    {
        
    }

    public JTableExScrollPane(JTableEx table)
    {
        setTable(table);
    }

    public void setTable(JTableEx table)
    {
        this.table = table;
        DynamicTableColumnModel model = table.getColumnModel();
        List<TableColumn> columns = model.getAvailableColumns();
        if (!columns.isEmpty())
        {
            JPopupMenu menu = new JPopupMenu();
            for (TableColumn column : columns)
            {
                JCheckBoxMenuItem item = new JCheckBoxMenuItem();
                item.setSelected(model.isVisible(column));
                item.setAction(new MenuAction(column));
                menu.add(item);
            }
            setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JButton(new CornerAction(menu)));
        }
        else
        {
            setCorner(JScrollPane.UPPER_RIGHT_CORNER, null);
        }
        setViewportView(table);
    }

    private class CornerAction extends AbstractAction
    {

        private JPopupMenu menu;

        public CornerAction(JPopupMenu menu)
        {
            super("...");
            this.menu = menu;
        }

        public void actionPerformed(ActionEvent e)
        {
            menu.show(table, table.getWidth() - menu.getWidth(), 0);
        }
        
    }

    private class MenuAction extends AbstractAction
    {

        private TableColumn column;

        public MenuAction(TableColumn column)
        {
            super(column.getHeaderValue().toString());
            this.column = column;
        }

        public void actionPerformed(ActionEvent e)
        {
            table.getColumnModel().toggleVisible(column);
        }
    }
}
