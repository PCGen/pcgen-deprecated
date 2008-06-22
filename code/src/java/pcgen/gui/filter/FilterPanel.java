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
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import pcgen.gui.core.UIContext;
import pcgen.gui.util.TextIcon;
import pcgen.util.PropertyFactory;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilterPanel extends JPanel
{

    private static final String filterString = PropertyFactory.getString("in_filter") +
            ":";
    private static final String clearString = PropertyFactory.getString("in_clear");
    private static final String advancedString = PropertyFactory.getString("in_demAdv");
    private final FilterPanelListener listener;
    private final JTextField textfield;
    private List<JToggleButton> filterbuttons;
    private FilterList filters;

    public FilterPanel(UIContext context, Class<?> filterclass,
                        FilterPanelListener listener)
    {
        this.textfield = new JTextField();
        this.listener = listener;
        initComponents();
        
        this.filters = context.getToggleFilters(filterclass);
        filters.addFilterListListener(
                new FilterListListener()
                {

                    public void filtersChanged(FilterListEvent event)
                    {
                        setFilterButtons(event.getNewFilters());
                    }

                });
        setFilterButtons(filters.getFilters());
    }

    private void setFilterButtons(List<ObjectFilter> filters)
    {
        List<ObjectFilter> toggledfilters = new LinkedList<ObjectFilter>();
        if (filterbuttons == null)
        {
            filterbuttons = new LinkedList<JToggleButton>();
        }
        else
        {
            for (JToggleButton button : filterbuttons)
            {
                if (button.isSelected())
                {
                    toggledfilters.add(((FilterAction)button.getAction()).getFilter());
                }
                remove(button);
            }
            filterbuttons.clear();
        }

        for (ObjectFilter filter : filters)
        {
            JToggleButton button = new JToggleButton(new FilterAction(filter));
            button.setSelected(toggledfilters.contains(filter));
            filterbuttons.add(button);
            add(button);
        }
    }

    private class FilterAction extends AbstractAction
    {

        private ObjectFilter filter;

        public FilterAction(ObjectFilter filter)
        {
            this.filter = filter;
            putValue(NAME, filter.getName());
            putValue(SHORT_DESCRIPTION, filter.getShortDescription());
            putValue(LONG_DESCRIPTION, filter.getLongDescription());
        }

        public ObjectFilter getFilter()
        {
            return filter;
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private void initComponents()
    {
        JToolBar toolbar = new JToolBar();

        toolbar.setRollover(true);
        toolbar.setFloatable(false);

        JButton button = new JButton();
        button.setFocusable(false);

        Icon icon = new TextIcon(button, filterString);
        button.setIcon(icon);

        icon = new TextIcon(button, clearString);
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

        add(toolbar);
    }

    private void updateFilter()
    {
        String text = textfield.getText();
        if (text.length() == 0)
        {
            listener.filtersChanged();
        }
        else
        {
            listener.filtersChanged();
        }
    }

    private class AdvancedAction extends AbstractAction
    {

        public AdvancedAction()
        {
            super(advancedString);
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
