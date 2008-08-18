/*
 * CharacterSheetDialog.java
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
 * Created on Aug 17, 2008, 3:11:39 PM
 */
package pcgen.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import pcgen.gui.PCGenActionMap;
import pcgen.gui.PCGenFrame;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.facade.EquipmentSetFacade;
import pcgen.gui.util.AbstractListMenu;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.ToolBarUtilities;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CharacterSheetDialog extends JDialog
{

    private final SaveAction saveAction;
    private final PrintAction printAction;
    private final JComboBox sheetBox;
    private final EquipmentSetMenu equipmentsetMenu;
    private final PCGenFrame frame;
    private final PCGenActionMap actionMap;

    public CharacterSheetDialog(PCGenFrame frame)
    {
        super(frame);
        this.frame = frame;
        this.actionMap = frame.getActionMap();
        this.saveAction = new SaveAction();
        this.printAction = new PrintAction();
        this.sheetBox = new JComboBox();
        this.equipmentsetMenu = new EquipmentSetMenu();
        initComponents();
    }

    private void initComponents()
    {
        sheetBox.setMaximumSize(new Dimension(200, 22));
        getContentPane().add(createToolBar(), BorderLayout.NORTH);
    }

    private JToolBar createToolBar()
    {
        JToolBar toolbar = ToolBarUtilities.createDefaultToolBar();
        toolbar.add(ToolBarUtilities.createToolBarButton(saveAction));
        toolbar.add(ToolBarUtilities.createToolBarButton(printAction));
        toolbar.addSeparator();
        toolbar.add(sheetBox);
        return toolbar;
    }

    private JMenuBar createMenuBar()
    {
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu(actionMap.get(PCGenActionMap.FILE_COMMAND));
        menu.add(new JMenuItem(saveAction));
        menu.add(new JMenuItem(printAction));
        menu.addSeparator();
        menu.add(new JMenuItem(new CloseAction()));
        menubar.add(menu);

        menu = new JMenu(actionMap.get(PCGenActionMap.VIEW_COMMAND));
        menu.add(equipmentsetMenu);

        return menubar;
    }

    public void setCharacter(CharacterFacade character)
    {
        equipmentsetMenu.setListModel(character.getEquipmentSets());
    }

    private void setEquipmentSet(EquipmentSetFacade equipmentSet)
    {

    }

    private class EquipmentSetMenu extends AbstractListMenu<EquipmentSetFacade>
    {

        private final ButtonGroup group = new ButtonGroup();

        public EquipmentSetMenu()
        {
            super(null);
        }

        @Override
        protected JMenuItem createMenuItem(EquipmentSetFacade equipset)
        {
            EquipmentSetMenuItem item = new EquipmentSetMenuItem(equipset);
            group.add(item);
            return item;
        }

        @Override
        public void remove(int index)
        {
            EquipmentSetMenuItem item = (EquipmentSetMenuItem) getComponent(index);
            group.remove(item);
            super.remove(index);
        }

        @Override
        public void setListModel(GenericListModel<EquipmentSetFacade> listModel)
        {
            super.setListModel(listModel);
            //Set the first MenuItem to be selected; usually the Default EquipmentSet
            Enumeration<AbstractButton> elements = group.getElements();
            if (elements.hasMoreElements())
            {
                elements.nextElement().setSelected(true);
            }
        }

        private class EquipmentSetMenuItem extends JRadioButtonMenuItem
                implements ActionListener
        {

            private final EquipmentSetFacade equipmentSet;

            public EquipmentSetMenuItem(EquipmentSetFacade equipset)
            {
                this.equipmentSet = equipset;
                addActionListener(this);
            }

            @Override
            public String getText()
            {
                return equipmentSet.toString();
            }

            public void actionPerformed(ActionEvent e)
            {
                setEquipmentSet(equipmentSet);
            }

        }
    }

    private class CharacterSheetPane extends JScrollPane implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class SaveAction extends ActionWrapper
    {

        public SaveAction()
        {
            super(actionMap.get(PCGenActionMap.SAVE_COMMAND));
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class PrintAction extends ActionWrapper
    {

        public PrintAction()
        {
            super(actionMap.get(PCGenActionMap.PRINT_COMMAND));
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class CloseAction extends ActionWrapper
    {

        public CloseAction()
        {
            super(actionMap.get(PCGenActionMap.CLOSE_COMMAND));
        }

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    /**
     * This class allows us to steal properties from existing Actions
     * rather than searching through property files.
     */
    private static abstract class ActionWrapper extends AbstractAction
    {

        public ActionWrapper(Action action)
        {
            putValueFromAction(action, NAME);
            putValueFromAction(action, SMALL_ICON);
            putValueFromAction(action, SHORT_DESCRIPTION);
            putValueFromAction(action, LONG_DESCRIPTION);
            putValueFromAction(action, MNEMONIC_KEY);
            putValueFromAction(action, ACCELERATOR_KEY);
            putValueFromAction(action, ACTION_COMMAND_KEY);
        }

        private void putValueFromAction(Action action, String key)
        {
            putValue(key, action.getValue(key));
        }

    }
}
