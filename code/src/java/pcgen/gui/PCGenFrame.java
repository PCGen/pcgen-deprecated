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

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
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
        setLayout(new BorderLayout());

        JComponent root = getRootPane();
        root.setActionMap(actionMap);
        root.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                         createInputMap(actionMap));
        setJMenuBar(new PCGenMenuBar(this));
        add(createToolBar(), BorderLayout.PAGE_START);
    }

    private static InputMap createInputMap(ActionMap actionMap)
    {
        InputMap inputMap = new InputMap();
        for (Object obj : actionMap.keys())
        {
            KeyStroke key = (KeyStroke) actionMap.get(obj).getValue(Action.ACCELERATOR_KEY);
            if (key != null)
            {
                inputMap.put(key, obj);
            }
        }
        return inputMap;
    }

    private JToolBar createToolBar()
    {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        toolbar.add(createToolBarButton(actionMap.get(PCGenActionMap.NEW_COMMAND)));
        toolbar.add(createToolBarButton(actionMap.get(PCGenActionMap.OPEN_COMMAND)));
        toolbar.add(createToolBarButton(actionMap.get(PCGenActionMap.CLOSE_COMMAND)));
        toolbar.add(createToolBarButton(actionMap.get(PCGenActionMap.SAVE_COMMAND)));
        toolbar.addSeparator();

        toolbar.add(createToolBarButton(actionMap.get(PCGenActionMap.PRINT_PREVIEW_COMMAND)));
        toolbar.add(createToolBarButton(actionMap.get(PCGenActionMap.PRINT_COMMAND)));
        toolbar.addSeparator();

        toolbar.add(createToolBarButton(actionMap.get(PCGenActionMap.OPTIONS_COMMAND)));
        toolbar.addSeparator();

        toolbar.add(createToolBarButton(actionMap.get(PCGenActionMap.HELP_CONTEXT_COMMAND)));
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

    public PCGenActionMap getActionMap()
    {
        return actionMap;
    }

    public GenericListModel<File> getRecentCharacters()
    {
        return null;
    }

    public GenericListModel<File> getRecentParties()
    {
        return null;
    }

    public GenericListModel<QuickSourceFacade> getQuickSources()
    {
        return null;
    }

}
