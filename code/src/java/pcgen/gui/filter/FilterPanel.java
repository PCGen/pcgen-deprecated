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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.undo.StateEditable;
import pcgen.gui.PCGenUIManager;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.GenericListModelWrapper;
import pcgen.gui.util.SimpleTextIcon;
import pcgen.gui.util.ToolBarUtilities;
import pcgen.gui.util.event.DocumentChangeAdapter;
import pcgen.gui.util.event.ListDataAdapter;
import pcgen.util.PropertyFactory;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FilterPanel extends JPanel implements StateEditable
{

    private static final String filterString = PropertyFactory.getString("in_filter") +
            ":";
    private static final String clearString = PropertyFactory.getString("in_clear");
    private static final String advancedString = PropertyFactory.getString("in_demAdv");
    private final JTextField textfield;
    private final JPanel buttonPanel;
    private final JPanel filterPanel;
    private FilterPanelListener panelListener = null;
    private ListDataListener listListener;
    private List<Filter> selectedFilters;
    private Class<?> filterClass;

    public FilterPanel()
    {
        this.buttonPanel = new JPanel(new FilterLayout());
        this.filterPanel = new JPanel();
        this.textfield = new JTextField();
        initComponents();
    }

    private void initComponents()
    {
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));
        setLayout(new BorderLayout());

        JToolBar toolbar = ToolBarUtilities.createDefaultToolBar();

        JButton button = ToolBarUtilities.createToolBarButton(null);

        Icon icon = new SimpleTextIcon(button, filterString);
        button.setIcon(icon);

        icon = new SimpleTextIcon(button, clearString);
        button.setRolloverIcon(icon);
        button.setPressedIcon(icon);

        button.addActionListener(
                new ActionListener()
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        textfield.setText("");
                    }

                });
        toolbar.add(button);

        textfield.getDocument().addDocumentListener(
                new DocumentChangeAdapter()
                {

                    @Override
                    public void documentChanged(DocumentEvent e)
                    {
                        fireApplyFilter();
                    }

                });

        toolbar.add(textfield);

        button = ToolBarUtilities.createToolBarButton(null);
        button.setIcon(new SimpleTextIcon(button, advancedString));
        button.addActionListener(
                new ActionListener()
                {

                    public void actionPerformed(ActionEvent e)
                    {
                    //advanced button action
                    }

                });
        toolbar.add(button);

        toolbar.addSeparator();

        filterPanel.add(toolbar);
        filterPanel.add(buttonPanel);
        add(filterPanel, BorderLayout.CENTER);

        final ArrowButton arrowbutton = new ArrowButton();
        arrowbutton.addMouseListener(
                new MouseAdapter()
                {

                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        boolean closed = !arrowbutton.isOpen();
                        arrowbutton.setOpen(closed);
                        if (closed)
                        {
                            add(filterPanel, BorderLayout.CENTER);
                        }
                        else
                        {
                            remove(filterPanel);
                        }
                        revalidate();
                    }

                });
        add(arrowbutton, BorderLayout.SOUTH);
    }

    private <T> void setFilterClass(Class<T> filterClass)
    {
        if (this.filterClass != null)
        {
            PCGenUIManager.getDisplayedFilters(this.filterClass).removeListDataListener(listListener);
        }
        this.filterClass = filterClass;
        GenericListModel<DisplayableFilter<? super T>> filterModel = PCGenUIManager.getDisplayedFilters(filterClass);
        final List<DisplayableFilter<? super T>> filterList = new GenericListModelWrapper<DisplayableFilter<? super T>>(filterModel);
        ListDataListener listener = new ListDataAdapter()
        {

            @Override
            public void listDataChanged(ListDataEvent e)
            {
                setFilterButtons(filterList);
            }

        };
        filterModel.addListDataListener(listener);
        listListener = listener;
        setFilterButtons(filterList);
    }

    public void setFilterPanelListener(FilterPanelListener listener)
    {
        this.panelListener = listener;
    }

    private <T> void setFilterButtons(List<DisplayableFilter<? super T>> filters)
    {
        buttonPanel.removeAll();

        boolean updateFilters = selectedFilters.retainAll(filters);

        for (DisplayableFilter<? super T> filter : filters)
        {
            JToggleButton button = new JToggleButton(new FilterAction(filter));
            button.setSelected(selectedFilters.contains(filter));
            buttonPanel.add(button);
        }

        if (updateFilters)
        {
            fireApplyFilter();
        }
    }

    public Hashtable<Object, Object> createState(Class<?> filterClass)
    {
        Hashtable<Object, Object> state = new Hashtable<Object, Object>();
        state.put("FilterClass", filterClass);
        state.put("SelectedFilters", new ArrayList<Filter>());
        return state;
    }

    public void storeState(Hashtable<Object, Object> state)
    {

    }

    @SuppressWarnings("unchecked")
    public void restoreState(Hashtable<?, ?> state)
    {
        Class<?> c = (Class<?>) state.get("FilterClass");
        selectedFilters = (List<Filter>) state.get("SelectedFilters");
        setFilterClass(c);
    }

    public Filter getFilter()
    {
        String text = textfield.getText();
        boolean qFilter = text != null && !text.equals("");
        return new ObjectFilter(text, qFilter);
    }

    private void fireApplyFilter()
    {
        if (panelListener != null)
        {
            ObjectFilter filter = (ObjectFilter) getFilter();
            panelListener.applyFilter(filter, filter.isQuickSearch());
        }
    }

    private class ObjectFilter implements Filter
    {

        private final boolean qFilter;
        private final String text;

        public ObjectFilter(String text, boolean qFilter)
        {
            this.qFilter = qFilter;
            this.text = text;
        }

        public boolean isQuickSearch()
        {
            return qFilter;
        }

        @SuppressWarnings("unchecked")
        public boolean accept(CharacterFacade character, Object object)
        {
            boolean accept = !qFilter || text.equals(object.toString());
            if (accept)
            {
                for (Filter filter : selectedFilters)
                {
                    if (!filter.accept(character, object))
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

        private DisplayableFilter<?> filter;

        public FilterAction(DisplayableFilter<?> filter)
        {
            this.filter = filter;
            putValue(NAME, filter.toString());
            putValue(SHORT_DESCRIPTION, filter.getDescription());
        }

        public void actionPerformed(ActionEvent e)
        {
            if (selectedFilters.contains(filter))
            {
                selectedFilters.remove(filter);
            }
            else
            {
                selectedFilters.add(filter);
            }
            fireApplyFilter();
        }

    }

    private static class ArrowButton extends JButton
    {

        private boolean entered = false;
        private boolean open = true;

        public ArrowButton()
        {
            setMinimumSize(new Dimension(6, 6));
            setPreferredSize(new Dimension(6, 6));
            setFocusPainted(false);
            setBorderPainted(false);
            setRequestFocusEnabled(false);
            addMouseListener(
                    new MouseAdapter()
                    {

                        @Override
                        public void mouseEntered(MouseEvent e)
                        {
                            entered = true;
                            repaint();
                        }

                        @Override
                        public void mouseExited(MouseEvent e)
                        {
                            entered = false;
                            repaint();
                        }

                    });
        }

        @Override
        public void setBorder(Border border)
        {

        }

        private static final int[] yup = {
            1,
            4,
            4
        };
        private static final int[] ydown = {
            4,
            1,
            1
        };

        @Override
        public void paint(Graphics g)
        {
            Color b;
            Color f;
            if (entered)
            {
                b = UIManager.getColor("controlDkShadow");
                f = UIManager.getColor("controlLtHighlight");
            }
            else
            {
                b = UIManager.getColor("control");
                f = UIManager.getColor("controlShadow");
            }
            g.setColor(b);
            g.fillRect(0, 0, getWidth(), getHeight());
            int center = getWidth() / 2;
            int[] xs = {
                center,
                center - 3,
                center + 3
            };
            int[] ys;
            if (open)
            {
                ys = yup;

            }
            else
            {
                ys = ydown;
            }
            g.setColor(f);
            g.drawPolygon(xs, ys, 3);
            g.fillPolygon(xs, ys, 3);
        }

        public void setOpen(boolean open)
        {
            this.open = open;
        }

        public boolean isOpen()
        {
            return open;
        }

    }

    private static class FilterLayout extends FlowLayout
    {

        public FilterLayout()
        {
            super(FlowLayout.LEFT);
        }

        @Override
        public Dimension preferredLayoutSize(Container target)
        {
            synchronized (target.getTreeLock())
            {
                Dimension dim = new Dimension(0, 0);
                int nmembers = target.getComponentCount();

                Insets insets = target.getInsets();
                int maxwidth = target.getWidth() - (insets.left + insets.right +
                        getHgap() * 2);
                int width = 0;
                int height = 0;
                int component = 0;
                for (int i = 0; i < nmembers; i++, component++)
                {
                    Component m = target.getComponent(i);
                    if (m.isVisible())
                    {
                        Dimension d = m.getPreferredSize();
                        if (component > 0)
                        {
                            if (width + d.width > maxwidth)
                            {
                                dim.width = Math.max(dim.width, width);
                                dim.height += height + getVgap();
                                width = 0;
                                height = 0;
                                component = 0;
                            }
                            width += getHgap();
                        }
                        height = Math.max(height, d.height);
                        width += d.width;
                    }
                }
                dim.width = Math.max(dim.width, width);
                dim.height += height;

                dim.width += insets.left + insets.right + getHgap() * 2;
                dim.height += insets.top + insets.bottom + getVgap() * 2;
                return dim;
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container target)
        {
            synchronized (target.getTreeLock())
            {
                Dimension dim = new Dimension(0, 0);
                int nmembers = target.getComponentCount();

                Insets insets = target.getInsets();
                int maxwidth = target.getWidth() - (insets.left + insets.right +
                        getHgap() * 2);
                int width = 0;
                int height = 0;
                int component = 0;
                for (int i = 0; i < nmembers; i++, component++)
                {
                    Component m = target.getComponent(i);
                    if (m.isVisible())
                    {
                        Dimension d = m.getMinimumSize();
                        if (component > 0)
                        {
                            if (width + d.width > maxwidth)
                            {
                                dim.width = Math.max(dim.width, width);
                                dim.height += height + getVgap();
                                width = 0;
                                height = 0;
                                component = 0;
                            }
                            width += getHgap();
                        }
                        height = Math.max(height, d.height);
                        width += d.width;
                    }
                }
                dim.width = Math.max(dim.width, width);
                dim.height += height;

                dim.width += insets.left + insets.right + getHgap() * 2;
                dim.height += insets.top + insets.bottom + getVgap() * 2;
                return dim;
            }
        }

    }
}
