/*
 * FilterPanel.java
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
 * Created on Jun 17, 2008, 11:46:31 PM
 */
package pcgen.gui.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import pcgen.gui.util.TextIcon;
import pcgen.util.PropertyFactory;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilterPanel extends JPanel
{

    private static final String filter = PropertyFactory.getString("in_filter") +
            ":";
    private static final String clear = PropertyFactory.getString("in_clear");
    private static final String advanced = PropertyFactory.getString("in_demAdv");
    private final JTextField textfield;
    private final FilterListener listener;

    public FilterPanel(FilterListener listener)
    {
        this.textfield = new JTextField();
        this.listener = listener;
        initComponents();
    }

    private void initComponents()
    {
        JToolBar toolbar = new JToolBar();

        toolbar.setRollover(true);
        toolbar.setFloatable(false);

        JButton button = new JButton();
        button.setFocusable(false);

        Icon icon = new TextIcon(button, filter);
        button.setIcon(icon);

        icon = new TextIcon(button, clear);
        button.setRolloverIcon(icon);
        button.setPressedIcon(icon);

        button.addActionListener(new ActionListener()
                         {

                             public void actionPerformed(ActionEvent e)
                             {
                                 textfield.setText("");
                             }

                         });
        toolbar.add(button);

        textfield.getDocument().addDocumentListener(
                new DocumentListener()
                {

                    public void insertUpdate(DocumentEvent e)
                    {
                        updateFilter();
                    }

                    public void removeUpdate(DocumentEvent e)
                    {
                        updateFilter();
                    }

                    public void changedUpdate(DocumentEvent e)
                    {
                        updateFilter();
                    }

                });

        toolbar.add(textfield);

        button = new JButton(new AdvancedAction());
        button.setFocusable(false);
        toolbar.add(button);

        toolbar.addSeparator();
    }

    private void updateFilter()
    {
        String text = textfield.getText();
        if (text.length() == 0)
        {
            listener.updateQFilter(null);
        }
        else
        {
            listener.updateQFilter(text);
        }
    }

    private class AdvancedAction extends AbstractAction
    {

        public AdvancedAction()
        {
            super(advanced);
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
