/*
 * PCGenFrame.java
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
 * Created on Aug 14, 2008, 1:00:34 PM
 */
package pcgen.gui;

import java.io.File;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import pcgen.gui.facade.QuickSourceFacade;
import pcgen.gui.util.GenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PCGenFrame extends JFrame
{

    private final PCGenActionMap actionMap;

    public PCGenFrame()
    {
        this.actionMap = new PCGenActionMap(this);
        initComponents();
    }

    private void initComponents()
    {
        JComponent root = getRootPane();
        root.setActionMap(actionMap);
        root.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                         createInputMap(actionMap));
    //root.getInputMap(root.)
    }

    private static InputMap createInputMap(ActionMap actionMap)
    {
        InputMap inputMap = new InputMap();
        for (Object obj : actionMap.keys())
        {
            Action action = actionMap.get(obj);
            KeyStroke key = (KeyStroke) action.getValue(action.ACCELERATOR_KEY);
            if (key != null)
            {
                inputMap.put(key, obj);
            }
        }
        return inputMap;
    }

    private JMenu createFileMenu()
    {
        JMenu menu = new JMenu(actionMap.get(PCGenActionMap.FILE_COMMAND));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.NEW_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.OPEN_COMMAND)));
        menu.add(new OpenRecentMenu(null));
        menu.addSeparator();

        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSE_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSEALL_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVE_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEAS_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEALL_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.REVERT_COMMAND)));
        menu.addSeparator();

        JMenu partyMenu = new JMenu(actionMap.get(PCGenActionMap.PARTY_COMMAND));
        partyMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.OPEN_PARTY_COMMAND)));
        partyMenu.add(new OpenRecentMenu(null));
        partyMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSE_PARTY_COMMAND)));
        partyMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVE_PARTY_COMMAND)));
        partyMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEAS_PARTY_COMMAND)));
        menu.add(partyMenu);
        menu.addSeparator();

        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.PRINT_PREVIEW_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.PRINT_COMMAND)));
        menu.addSeparator();

        JMenu exportMenu = new JMenu(actionMap.get(PCGenActionMap.EXPORT_COMMAND));
        exportMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EXPORT_STANDARD_COMMAND)));
        exportMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EXPORT_PDF_COMMAND)));
        exportMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EXPORT_TEXT_COMMAND)));
        menu.add(exportMenu);

        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.EXIT_COMMAND)));
        return menu;
    }

    private JMenu createToolsMenu()
    {
        JMenu menu = new JMenu(actionMap.get(PCGenActionMap.TOOLS_COMMAND));
        menu.add(new QuickSourceMenu(null));
        menu.addSeparator();

        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.FILTERS_COMMAND)));
        menu.addSeparator();

        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.OPTIONS_COMMAND)));
        return menu;
    }

    private JToolBar createToolBar()
    {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        //toolbar.add(createToolBarButton(new NewAction()));
        return toolbar;
    }

    private static JButton createToolBarButton(Action action)
    {
        JButton button = new JButton();
        button.putClientProperty("hideActionText", true);
        button.setFocusable(false);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setAction(action);
        return button;
    }

    private class OpenRecentMenu extends ListMenu<File>
    {

        public OpenRecentMenu(GenericListModel<File> listModel)
        {
            super(actionMap.get(PCGenActionMap.OPEN_RECENT_COMMAND), listModel);
        }

        @Override
        protected JMenuItem createMenuItem(File item)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class QuickSourceMenu extends ListMenu<QuickSourceFacade>
    {

        public QuickSourceMenu(GenericListModel<QuickSourceFacade> listModel)
        {
            super(actionMap.get(PCGenActionMap.SOURCES_COMMAND), listModel);
            addSeparator();
            add(new JMenuItem(actionMap.get(PCGenActionMap.SOURCES_ADVANCED_COMMAND)));
        }

        protected JMenuItem createMenuItem(QuickSourceFacade source)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private static abstract class ListMenu<E> extends JMenu implements ListDataListener
    {

        private GenericListModel<E> listModel;

        public ListMenu(Action action, GenericListModel<E> listModel)
        {
            super(action);
            this.listModel = listModel;
            for (int x = 0; x < listModel.getSize(); x++)
            {
                add(createMenuItem(listModel.getElementAt(x)));
            }
            listModel.addListDataListener(this);
        }

        protected abstract JMenuItem createMenuItem(E item);

        public void intervalAdded(ListDataEvent e)
        {
            for (int x = e.getIndex0(); x <= e.getIndex1(); x++)
            {
                add(createMenuItem(listModel.getElementAt(x)), x);
            }
        }

        public void intervalRemoved(ListDataEvent e)
        {
            for (int x = e.getIndex0(); x <= e.getIndex1(); x++)
            {
                remove(e.getIndex0());
            }
        }

        public void contentsChanged(ListDataEvent e)
        {
            intervalRemoved(e);
            intervalAdded(e);
        }

    }
}
