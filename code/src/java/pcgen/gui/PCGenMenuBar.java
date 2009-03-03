/*
 * PCGenMenuBar.java
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
 * Created on Aug 16, 2008, 3:19:16 PM
 */
package pcgen.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.facade.EquipmentSetFacade;
import pcgen.gui.facade.SourceFacade;
import pcgen.gui.facade.TempBonusFacade;
import pcgen.gui.tools.CharacterSelectionListener;
import pcgen.gui.util.AbstractListMenu;
import pcgen.gui.util.ComboListMenu;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PCGenMenuBar extends JMenuBar implements CharacterSelectionListener
{

    private final PCGenFrame frame;
    private final PCGenActionMap actionMap;
    private final EquipmentSetMenu equipmentMenu;
    private final TempBonusMenu tempMenu;
    private CharacterFacade character;

    public PCGenMenuBar(PCGenFrame frame)
    {
        this.frame = frame;
        this.actionMap = frame.getActionMap();
        this.equipmentMenu = new EquipmentSetMenu();
        this.tempMenu = new TempBonusMenu();
        initComponents();
    }

    private void initComponents()
    {
        add(createFileMenu());
        add(createViewMenu());
        add(createToolsMenu());
        add(createHelpMenu());
    }

    private JMenu createFileMenu()
    {
        JMenu fileMenu = new JMenu(actionMap.get(PCGenActionMap.FILE_COMMAND));
        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.NEW_COMMAND)));
        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.OPEN_COMMAND)));
        fileMenu.add(new OpenRecentMenu());
        fileMenu.addSeparator();

        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSE_COMMAND)));
        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSEALL_COMMAND)));
        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVE_COMMAND)));
        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEAS_COMMAND)));
        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEALL_COMMAND)));
        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.REVERT_COMMAND)));
        fileMenu.addSeparator();

        JMenu partyMenu = new JMenu(actionMap.get(PCGenActionMap.PARTY_COMMAND));
        partyMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.OPEN_PARTY_COMMAND)));
        partyMenu.add(new OpenRecentPartyMenu());
        partyMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSE_PARTY_COMMAND)));
        partyMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVE_PARTY_COMMAND)));
        partyMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEAS_PARTY_COMMAND)));
        fileMenu.add(partyMenu);
        fileMenu.addSeparator();

        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.PRINT_PREVIEW_COMMAND)));
        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.PRINT_COMMAND)));
        fileMenu.addSeparator();

        JMenu exportMenu = new JMenu(actionMap.get(PCGenActionMap.EXPORT_COMMAND));
        exportMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EXPORT_STANDARD_COMMAND)));
        exportMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EXPORT_PDF_COMMAND)));
        exportMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EXPORT_TEXT_COMMAND)));
        fileMenu.add(exportMenu);

        fileMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EXIT_COMMAND)));
        return fileMenu;
    }

    private JMenu createViewMenu()
    {
        JMenu menu = new JMenu(actionMap.get(PCGenActionMap.VIEW_COMMAND));

        JMenu filtersMenu = new JMenu(actionMap.get(PCGenActionMap.FILTERS_COMMAND));
        filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.KIT_FILTERS_COMMAND)));
        filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.RACE_FILTERS_COMMAND)));
        filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLASS_FILTERS_COMMAND)));
        filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.ABILITY_FILTERS_COMMAND)));
        filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SKILL_FILTERS_COMMAND)));
        filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EQUIPMENT_FILTERS_COMMAND)));
        filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SPELL_FILTERS_COMMAND)));
        filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.TEMPLATE_FILTERS_COMMAND)));
        menu.add(filtersMenu);
        menu.addSeparator();
        menu.add(equipmentMenu);
        menu.add(tempMenu);
        menu.add(new ComboListMenu<File>(actionMap.get(PCGenActionMap.CSHEET_COMMAND),
                                         frame.getCharacterSheets()));
        return menu;
    }

    private JMenu createToolsMenu()
    {
        JMenu menu = new JMenu(actionMap.get(PCGenActionMap.TOOLS_COMMAND));
        menu.add(new QuickSourceMenu());
        menu.addSeparator();

        JMenu generatorsMenu = new JMenu(actionMap.get(PCGenActionMap.GENERATORS_COMMAND));
        generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.TREASURE_GENERATORS_COMMAND)));
        generatorsMenu.addSeparator();
        generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.STAT_GENERATORS_COMMAND)));
        generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.RACE_GENERATORS_COMMAND)));
        generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLASS_GENERATORS_COMMAND)));
        generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.ABILITY_GENERATORS_COMMAND)));
        generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SKILL_GENERATORS_COMMAND)));
        generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EQUIPMENT_GENERATORS_COMMAND)));
        generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SPELL_GENERATORS_COMMAND)));
        generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.TEMPLATE_GENERATORS_COMMAND)));
        menu.add(generatorsMenu);
        menu.addSeparator();

        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.OPTIONS_COMMAND)));
        return menu;
    }

    private JMenu createHelpMenu()
    {
        JMenu menu = new JMenu(actionMap.get(PCGenActionMap.HELP_COMMAND));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_CONTEXT_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_DOCS_COMMAND)));
        menu.addSeparator();
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_OGL_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_SPONSORS_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_TIPOFTHEDAY_COMMAND)));
        menu.addSeparator();
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_ABOUT_COMMAND)));
        return menu;
    }

    public void setCharacter(CharacterFacade character)
    {
        this.character = character;
        equipmentMenu.setListModel(character.getEquipmentSets());
        tempMenu.setListModel(character.getTempBonuses());
    }

    private class OpenRecentMenu extends AbstractListMenu<File>
    {

        public OpenRecentMenu()
        {
            super(actionMap.get(PCGenActionMap.OPEN_RECENT_COMMAND),
                  frame.getRecentCharacters());
        }

        @Override
        protected JMenuItem createMenuItem(File item)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class OpenRecentPartyMenu extends AbstractListMenu<File>
    {

        public OpenRecentPartyMenu()
        {
            super(actionMap.get(PCGenActionMap.OPEN_RECENT_PARTY_COMMAND),
                  frame.getRecentParties());
        }

        @Override
        protected JMenuItem createMenuItem(File item)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class QuickSourceMenu extends AbstractListMenu<SourceFacade>
    {

        public QuickSourceMenu()
        {
            super(actionMap.get(PCGenActionMap.SOURCES_COMMAND),
                  frame.getQuickSources());
            addSeparator();
            add(new JMenuItem(actionMap.get(PCGenActionMap.SOURCES_ADVANCED_COMMAND)));
        }

        protected JMenuItem createMenuItem(SourceFacade source)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class EquipmentSetMenu extends ComboListMenu<EquipmentSetFacade>
    {

        public EquipmentSetMenu()
        {
            super(null);
        }

    }

    private class TempBonusMenu extends AbstractListMenu<TempBonusFacade>
            implements ItemListener
    {

        public TempBonusMenu()
        {
            super(null);
        }

        @Override
        protected JMenuItem createMenuItem(TempBonusFacade item)
        {
            return new CheckBoxMenuItem(item, character.isTempBonusApplied(item),
                                        this);
        }

        public void itemStateChanged(ItemEvent e)
        {
            TempBonusFacade bonus = (TempBonusFacade) e.getItemSelectable().getSelectedObjects()[0];
            character.applyTempBonus(bonus,
                                     e.getStateChange() == ItemEvent.SELECTED);
        }

    }
}
