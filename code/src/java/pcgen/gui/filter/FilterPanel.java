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
import java.util.ArrayList;
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
public class FilterPanel<E> extends JPanel
{

    private static final String filterString = PropertyFactory.getString("in_filter") +
            ":";
    private static final String clearString = PropertyFactory.getString("in_clear");
    private static final String advancedString = PropertyFactory.getString("in_demAdv");
    private final List<Filter<? super E>> selectedFilters;
    private final List<JToggleButton> filterbuttons;
    private final FilterPanelListener<E> listener;
    private final JTextField textfield;

    public FilterPanel(UIContext context, Class<E> filterclass,
                        FilterPanelListener<E> listener)
    {
        this.textfield = new JTextField();
        this.listener = listener;
        initComponents();

        FilterList<E> filters = context.getToggleFilters(filterclass);
        filters.addFilterListListener(
                new FilterListListener<E>()
                {

                    public void filtersChanged(FilterListEvent<E> event)
                    {
                        setFilterButtons(event.getNewFilters());
                    }

                });
        this.selectedFilters = new ArrayList<Filter<? super E>>();
        this.filterbuttons = new LinkedList<JToggleButton>();
        setFilterButtons(filters.getFilters());
    }

    private void setFilterButtons(List<NamedFilter<? super E>> filters)
    {
        for (JToggleButton button : filterbuttons)
        {
            remove(button);
        }
        filterbuttons.clear();

        boolean updateFilters = selectedFilters.retainAll(filters);

        for (NamedFilter<? super E> filter : filters)
        {
            JToggleButton button = new JToggleButton(new FilterAction(filter));
            button.setSelected(selectedFilters.contains(filter));
            filterbuttons.add(button);
            add(button);
        }

        if (updateFilters)
        {
            fireApplyFilter();
        }
    }

    private void fireApplyFilter()
    {
        String text = textfield.getText();
        boolean qFilter = text != null && !text.equals("");
        listener.applyFilter(new ObjectFilter(text, qFilter), qFilter);
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
                        fireApplyFilter();
                    }

                    public void removeUpdate(DocumentEvent e)
                    {
                        fireApplyFilter();
                    }

                    public void changedUpdate(DocumentEvent e)
                    {
                        fireApplyFilter();
                    }

                });

        toolbar.add(textfield);

        button = new JButton(new AdvancedAction());
        button.setFocusable(false);
        toolbar.add(button);

        toolbar.addSeparator();

        add(toolbar);
    }

    private class ObjectFilter implements Filter<E>
    {

        private final boolean qFilter;
        private final String text;

        public ObjectFilter(String text, boolean qFilter)
        {
            this.qFilter = qFilter;
            this.text = text;
        }

        public boolean accept(E object)
        {
            boolean accept = qFilter && text.equals(object.toString());
            if (accept)
            {
                for (Filter<? super E> filter : selectedFilters)
                {
                    if (!filter.accept(object))
                    {
                        accept = false;
                        break;
                    }
                }
            }
            return accept;
        }

    }

    private class FilterAction extends AbstractAction
    {

        private NamedFilter<? super E> filter;

        public FilterAction(NamedFilter<? super E> filter)
        {
            this.filter = filter;
            putValue(NAME, filter.getName());
            putValue(SHORT_DESCRIPTION, filter.getShortDescription());
            putValue(LONG_DESCRIPTION, filter.getLongDescription());
        }

        public NamedFilter getFilter()
        {
            return filter;
        }

        public void actionPerformed(ActionEvent e)
        {
            selectedFilters.add(filter);
            fireApplyFilter();
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
