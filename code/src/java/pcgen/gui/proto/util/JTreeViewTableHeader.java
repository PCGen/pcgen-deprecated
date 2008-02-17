/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Created on Feb 16, 2008, 8:27:21 PM
 */
package pcgen.gui.proto.util;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Petba
 */
public class JTreeViewTableHeader extends JTableHeader
{

    @Override
    public TableCellRenderer createDefaultRenderer()
    {

        return new SortingHeaderRenderer();
    }

    @Override
    public void setTable(JTable table)
    {
        super.setTable(table);
        columnModel = table.getColumnModel();
    }

    private class SortingHeaderRenderer extends JButton implements TableCellRenderer
    {

        private ButtonModel emptyModel = new DefaultButtonModel();
        private ButtonModel usedModel = new DefaultButtonModel();
        private TableColumn usedcolumn = null;

        private SortingHeaderRenderer()
        {
            ButtonModelHandler handler = new ButtonModelHandler();
            JTreeViewTableHeader.this.addMouseListener(handler);
            JTreeViewTableHeader.this.addMouseMotionListener(handler);
        //this.setRolloverEnabled(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if (usedcolumn != null && usedcolumn.getHeaderValue() == value)
            {
                setModel(usedModel);
            }
            else
            {
                setModel(emptyModel);
            }
            setText(value.toString());
            return this;
        }

        private class ButtonModelHandler implements MouseListener, MouseMotionListener
        {

            private TableColumn getColumn(MouseEvent e)
            {
                TableColumnModel model = JTreeViewTableHeader.this.getColumnModel();
                return model.getColumn(model.getColumnIndexAtX(e.getX()));
            }

            public void mouseClicked(MouseEvent e)
            {
                SortingHeaderRenderer.this.doClick();
            }

            public void mousePressed(MouseEvent e)
            {
                usedModel.setPressed(true);
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

            public void mouseDragged(MouseEvent e)
            {
            }

            public void mouseMoved(MouseEvent e)
            {
                usedcolumn = getColumn(e);
            }
        }
    }
}
