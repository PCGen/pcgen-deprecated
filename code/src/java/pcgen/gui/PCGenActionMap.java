/*
 * PCGenActionMap.java
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
 * Created on Aug 14, 2008, 3:51:27 PM
 */
package pcgen.gui;

import java.awt.event.ActionEvent;
import javax.swing.ActionMap;
import pcgen.gui.facade.AbilityFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.ItemFacade;
import pcgen.gui.facade.KitFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.facade.SpellFacade;
import pcgen.gui.facade.StatFacade;
import pcgen.gui.facade.TemplateFacade;
import pcgen.gui.tools.PCGenAction;
import pcgen.gui.tools.ResourceManager.Icons;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PCGenActionMap extends ActionMap
{

    public static final String FILE_COMMAND = "file";
    public static final String NEW_COMMAND = FILE_COMMAND + ".new";
    public static final String OPEN_COMMAND = FILE_COMMAND + ".open";
    public static final String OPEN_RECENT_COMMAND = FILE_COMMAND +
            ".openrecent";
    public static final String CLOSE_COMMAND = FILE_COMMAND + ".close";
    public static final String CLOSEALL_COMMAND = FILE_COMMAND + ".closeall";
    public static final String SAVE_COMMAND = FILE_COMMAND + ".save";
    public static final String SAVEAS_COMMAND = FILE_COMMAND + ".saveas";
    public static final String SAVEALL_COMMAND = FILE_COMMAND + ".saveall";
    public static final String REVERT_COMMAND = FILE_COMMAND +
            ".reverttosaved";
    public static final String PARTY_COMMAND = FILE_COMMAND + ".party";
    public static final String OPEN_PARTY_COMMAND = PARTY_COMMAND + ".open";
    public static final String OPEN_RECENT_PARTY_COMMAND = PARTY_COMMAND +
            ".openrecent";
    public static final String CLOSE_PARTY_COMMAND = PARTY_COMMAND + ".close";
    public static final String SAVE_PARTY_COMMAND = PARTY_COMMAND + ".save";
    public static final String SAVEAS_PARTY_COMMAND =
            PARTY_COMMAND + ".saveas";
    public static final String PRINT_PREVIEW_COMMAND = FILE_COMMAND +
            ".printpreview";
    public static final String PRINT_COMMAND = FILE_COMMAND + ".print";
    public static final String EXPORT_COMMAND = FILE_COMMAND + ".export";
    public static final String EXPORT_STANDARD_COMMAND =
            EXPORT_COMMAND + ".standard";
    public static final String EXPORT_PDF_COMMAND = EXPORT_COMMAND + ".pdf";
    public static final String EXPORT_TEXT_COMMAND = EXPORT_COMMAND + ".text";
    public static final String EXIT_COMMAND = FILE_COMMAND + ".exit";
    public static final String VIEW_COMMAND = "view";
    public static final String FILTERS_COMMAND = VIEW_COMMAND + ".filters";
    public static final String KIT_FILTERS_COMMAND = FILTERS_COMMAND +
            ".kit";
    public static final String RACE_FILTERS_COMMAND = FILTERS_COMMAND +
            ".race";
    public static final String TEMPLATE_FILTERS_COMMAND = FILTERS_COMMAND +
            ".template";
    public static final String CLASS_FILTERS_COMMAND = FILTERS_COMMAND +
            ".class";
    public static final String ABILITY_FILTERS_COMMAND = FILTERS_COMMAND +
            ".ability";
    public static final String SKILL_FILTERS_COMMAND = FILTERS_COMMAND +
            ".skill";
    public static final String EQUIPMENT_FILTERS_COMMAND = FILTERS_COMMAND +
            ".equipment";
    public static final String SPELL_FILTERS_COMMAND = FILTERS_COMMAND +
            ".spell";
    public static final String CSHEET_COMMAND = VIEW_COMMAND + ".csheet";
    public static final String TOOLS_COMMAND = "tools";
    public static final String SOURCES_COMMAND = TOOLS_COMMAND + ".sources";
    public static final String SOURCES_ADVANCED_COMMAND = SOURCES_COMMAND +
            ".advanced";
    public static final String GENERATORS_COMMAND = TOOLS_COMMAND +
            ".generators";
    public static final String TREASURE_GENERATORS_COMMAND = GENERATORS_COMMAND +
            ".treasure";
    public static final String RACE_GENERATORS_COMMAND = GENERATORS_COMMAND +
            ".race";
    public static final String TEMPLATE_GENERATORS_COMMAND = GENERATORS_COMMAND +
            ".template";
    public static final String CLASS_GENERATORS_COMMAND = GENERATORS_COMMAND +
            ".class";
    public static final String STAT_GENERATORS_COMMAND = GENERATORS_COMMAND +
            ".stat";
    public static final String ABILITY_GENERATORS_COMMAND = GENERATORS_COMMAND +
            ".ability";
    public static final String SKILL_GENERATORS_COMMAND = GENERATORS_COMMAND +
            ".skill";
    public static final String EQUIPMENT_GENERATORS_COMMAND = GENERATORS_COMMAND +
            ".equipment";
    public static final String SPELL_GENERATORS_COMMAND = GENERATORS_COMMAND +
            ".spell";
    public static final String OPTIONS_COMMAND = TOOLS_COMMAND + ".options";
    public static final String HELP_COMMAND = "help";
    public static final String HELP_CONTEXT_COMMAND = HELP_COMMAND +
            ".context";
    public static final String HELP_DOCS_COMMAND = HELP_COMMAND + ".docs";
    public static final String HELP_OGL_COMMAND = HELP_COMMAND + ".ogl";
    public static final String HELP_SPONSORS_COMMAND = HELP_COMMAND +
            ".sponsors";
    public static final String HELP_TIPOFTHEDAY_COMMAND = HELP_COMMAND +
            ".tod";
    public static final String HELP_ABOUT_COMMAND = HELP_COMMAND + ".about";
    private PCGenFrame frame;

    public PCGenActionMap(PCGenFrame frame)
    {
        this.frame = frame;
        initActions();
    }

    private void initActions()
    {
        put(FILE_COMMAND, new FileAction());
        put(NEW_COMMAND, new NewAction());
        put(OPEN_COMMAND, new OpenAction());
        put(OPEN_RECENT_COMMAND, new OpenRecentAction());
        put(CLOSE_COMMAND, new CloseAction());
        put(CLOSEALL_COMMAND, new CloseAllAction());
        put(SAVE_COMMAND, new SaveAction());
        put(SAVEAS_COMMAND, new SaveAsAction());
        put(SAVEALL_COMMAND, new SaveAllAction());
        put(REVERT_COMMAND, new RevertAction());

        put(PARTY_COMMAND, new PartyAction());
        put(OPEN_PARTY_COMMAND, new OpenPartyAction());
        put(OPEN_RECENT_PARTY_COMMAND, new OpenRecentAction());
        put(CLOSE_PARTY_COMMAND, new ClosePartyAction());
        put(SAVE_PARTY_COMMAND, new SavePartyAction());
        put(SAVEAS_PARTY_COMMAND, new SaveAsPartyAction());

        put(PRINT_PREVIEW_COMMAND, new PrintPreviewAction());
        put(PRINT_COMMAND, new PrintAction());
        put(EXPORT_COMMAND, new ExportAction());
        put(EXPORT_STANDARD_COMMAND, new ExportStandardAction());
        put(EXPORT_PDF_COMMAND, new ExportPDFAction());
        put(EXPORT_TEXT_COMMAND, new ExportTextAction());
        put(EXIT_COMMAND, new ExitAction());

        put(VIEW_COMMAND, new ViewAction());
        put(FILTERS_COMMAND, new FiltersAction());
        put(KIT_FILTERS_COMMAND,
            new DefaultFiltersAction("mnuViewFiltersKit", KIT_FILTERS_COMMAND,
                                     KitFacade.class));
        put(RACE_FILTERS_COMMAND,
            new DefaultFiltersAction("mnuViewFiltersRace", RACE_FILTERS_COMMAND,
                                     RaceFacade.class));
        put(TEMPLATE_FILTERS_COMMAND,
            new DefaultFiltersAction("mnuViewFiltersTemplate",
                                     TEMPLATE_FILTERS_COMMAND,
                                     TemplateFacade.class));
        put(CLASS_FILTERS_COMMAND,
            new DefaultFiltersAction("mnuViewFiltersClass",
                                     CLASS_FILTERS_COMMAND,
                                     ClassFacade.class));
        put(ABILITY_FILTERS_COMMAND,
            new DefaultFiltersAction("mnuViewFiltersAbility",
                                     ABILITY_FILTERS_COMMAND,
                                     AbilityFacade.class));
        put(SKILL_FILTERS_COMMAND,
            new DefaultFiltersAction("mnuViewFiltersSkill",
                                     SKILL_FILTERS_COMMAND,
                                     SkillFacade.class));
        put(EQUIPMENT_FILTERS_COMMAND,
            new DefaultFiltersAction("mnuViewFiltersEquipment",
                                     EQUIPMENT_FILTERS_COMMAND,
                                     ItemFacade.class));
        put(SPELL_FILTERS_COMMAND,
            new DefaultFiltersAction("mnuViewFiltersSpell",
                                     SPELL_GENERATORS_COMMAND,
                                     SpellFacade.class));
        put(CSHEET_COMMAND, new CharacterSheetAction());

        put(TOOLS_COMMAND, new ToolsAction());
        put(SOURCES_COMMAND, new SourcesAction());
        put(SOURCES_ADVANCED_COMMAND, new AdvancedSourcesAction());
        put(GENERATORS_COMMAND, new GeneratorsAction());
        put(TREASURE_GENERATORS_COMMAND, new TreasureGeneratorsAction());
        put(STAT_GENERATORS_COMMAND,
            new DefaultGeneratorsAction("mnuToolsGeneratorStat",
                                        STAT_GENERATORS_COMMAND,
                                        StatFacade.class));
        put(RACE_GENERATORS_COMMAND,
            new DefaultGeneratorsAction("mnuToolsGeneratorRace",
                                        RACE_GENERATORS_COMMAND,
                                        RaceFacade.class));
        put(TEMPLATE_GENERATORS_COMMAND,
            new DefaultGeneratorsAction("mnuToolsGeneratorTemplate",
                                        TEMPLATE_GENERATORS_COMMAND,
                                        TemplateFacade.class));
        put(CLASS_GENERATORS_COMMAND,
            new DefaultGeneratorsAction("mnuToolsGeneratorClass",
                                        CLASS_GENERATORS_COMMAND,
                                        ClassFacade.class));
        put(ABILITY_GENERATORS_COMMAND,
            new DefaultGeneratorsAction("mnuToolsGeneratorAbility",
                                        ABILITY_GENERATORS_COMMAND,
                                        AbilityFacade.class));
        put(SKILL_GENERATORS_COMMAND,
            new DefaultGeneratorsAction("mnuToolsGeneratorSkill",
                                        SKILL_GENERATORS_COMMAND,
                                        SkillFacade.class));
        put(EQUIPMENT_GENERATORS_COMMAND,
            new DefaultGeneratorsAction("mnuToolsGeneratorEquipment",
                                        EQUIPMENT_GENERATORS_COMMAND,
                                        ItemFacade.class));
        put(SPELL_GENERATORS_COMMAND,
            new DefaultGeneratorsAction("mnuToolsGeneratorSpell",
                                        SPELL_GENERATORS_COMMAND,
                                        SpellFacade.class));
        put(OPTIONS_COMMAND, new OptionsAction());

        put(HELP_COMMAND, new HelpAction());
        put(HELP_CONTEXT_COMMAND, new ContextHelpAction());
        put(HELP_DOCS_COMMAND, new DocsHelpAction());
        put(HELP_OGL_COMMAND, new OGLHelpAction());
        put(HELP_SPONSORS_COMMAND, new SponsorsHelpAction());
        put(HELP_TIPOFTHEDAY_COMMAND, new TipOfTheDayHelpAction());
        put(HELP_ABOUT_COMMAND, new AboutHelpAction());
    }

    private class FileAction extends PCGenAction
    {

        public FileAction()
        {
            super("mnuFile");
        }

    }

    private class NewAction extends PCGenAction
    {

        public NewAction()
        {
            super("mnuFileNew", NEW_COMMAND, "shortcut N", Icons.New16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class OpenAction extends PCGenAction
    {

        public OpenAction()
        {
            super("mnuFileOpen", OPEN_COMMAND, "shortcut O", Icons.Open16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class OpenRecentAction extends PCGenAction
    {

        public OpenRecentAction()
        {
            super("mnuOpenRecent");
        }

    }

    private class CloseAction extends PCGenAction
    {

        public CloseAction()
        {
            super("mnuFileClose", CLOSE_COMMAND, "shortcut W", Icons.Close16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class CloseAllAction extends PCGenAction
    {

        public CloseAllAction()
        {
            super("mnuFileCloseAll", CLOSEALL_COMMAND, Icons.CloseAll16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class SaveAction extends PCGenAction
    {

        public SaveAction()
        {
            super("mnuFileSave", SAVE_COMMAND, "shortcut S", Icons.Save16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class SaveAsAction extends PCGenAction
    {

        public SaveAsAction()
        {
            super("mnuFileSaveAs", SAVEAS_COMMAND, "shift-shortcut S",
                  Icons.SaveAs16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class SaveAllAction extends PCGenAction
    {

        public SaveAllAction()
        {
            super("mnuFileSaveAll", SAVEALL_COMMAND, Icons.SaveAll16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class RevertAction extends PCGenAction
    {

        public RevertAction()
        {
            super("mnuFileRevertToSaved", REVERT_COMMAND, "shortcut R");
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class PartyAction extends PCGenAction
    {

        public PartyAction()
        {
            super("mnuFileParty");
        }

    }

    private class OpenPartyAction extends PCGenAction
    {

        public OpenPartyAction()
        {
            super("mnuFilePartyOpen", OPEN_PARTY_COMMAND, Icons.Open16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class ClosePartyAction extends PCGenAction
    {

        public ClosePartyAction()
        {
            super("mnuFilePartyClose", CLOSE_PARTY_COMMAND, Icons.Close16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class SavePartyAction extends PCGenAction
    {

        public SavePartyAction()
        {
            super("mnuFilePartySave", SAVE_PARTY_COMMAND, Icons.Save16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class SaveAsPartyAction extends PCGenAction
    {

        public SaveAsPartyAction()
        {
            super("mnuFilePartySaveAs", SAVEAS_PARTY_COMMAND, Icons.SaveAs16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class PrintPreviewAction extends PCGenAction
    {

        public PrintPreviewAction()
        {
            super("mnuFilePrintPreview", PRINT_PREVIEW_COMMAND,
                  Icons.PrintPreview16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class PrintAction extends PCGenAction
    {

        public PrintAction()
        {
            super("mnuFilePrint", PRINT_COMMAND, "shortcut P", Icons.Print16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class ExportAction extends PCGenAction
    {

        public ExportAction()
        {
            super("mnuFileExport", Icons.Export16);
        }

    }

    private class ExportStandardAction extends PCGenAction
    {

        public ExportStandardAction()
        {
            super("mnuFileExportStandard", EXPORT_STANDARD_COMMAND);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class ExportPDFAction extends PCGenAction
    {

        public ExportPDFAction()
        {
            super("mnuFileExportPDF", EXPORT_PDF_COMMAND);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class ExportTextAction extends PCGenAction
    {

        public ExportTextAction()
        {
            super("mnuFileExportText", EXPORT_TEXT_COMMAND);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class ExitAction extends PCGenAction
    {

        public ExitAction()
        {
            super("mnuFileExit", EXIT_COMMAND, "shortcut Q");
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class ViewAction extends PCGenAction
    {

        public ViewAction()
        {
            super("mnuView", VIEW_COMMAND);
        }

    }

    private class CharacterSheetAction extends PCGenAction
    {

        public CharacterSheetAction()
        {
            super(null);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class ToolsAction extends PCGenAction
    {

        public ToolsAction()
        {
            super("mnuTools", TOOLS_COMMAND);
        }

    }

    private class SourcesAction extends PCGenAction
    {

        public SourcesAction()
        {
            super(null);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class AdvancedSourcesAction extends PCGenAction
    {

        public AdvancedSourcesAction()
        {
            super(null);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class FiltersAction extends PCGenAction
    {

        public FiltersAction()
        {
            super("mnuViewFilters");
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class GeneratorsAction extends PCGenAction
    {

        public GeneratorsAction()
        {
            super(null);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {

        }

    }

    private class TreasureGeneratorsAction extends PCGenAction
    {

        public TreasureGeneratorsAction()
        {
            super("mnuToolsGeneratorTreasure", TREASURE_GENERATORS_COMMAND,
                  "shortcut T");
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class OptionsAction extends PCGenAction
    {

        public OptionsAction()
        {
            super(null);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class HelpAction extends PCGenAction
    {

        public HelpAction()
        {
            super("mnuHelp", HELP_COMMAND);
        }

    }

    private class ContextHelpAction extends PCGenAction
    {

        public ContextHelpAction()
        {
            super("mnuHelpContext", HELP_CONTEXT_COMMAND, Icons.ContextualHelp16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class DocsHelpAction extends PCGenAction
    {

        public DocsHelpAction()
        {
            super("mnuHelpDocumentation", HELP_DOCS_COMMAND, Icons.Help16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class OGLHelpAction extends PCGenAction
    {

        public OGLHelpAction()
        {
            super("mnuHelpOGL", HELP_OGL_COMMAND);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class SponsorsHelpAction extends PCGenAction
    {

        public SponsorsHelpAction()
        {
            super("mnuHelpSponsors", HELP_SPONSORS_COMMAND);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class TipOfTheDayHelpAction extends PCGenAction
    {

        public TipOfTheDayHelpAction()
        {
            super("mnuHelpTipOfTheDay", HELP_TIPOFTHEDAY_COMMAND,
                  Icons.TipOfTheDay16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class AboutHelpAction extends PCGenAction
    {

        public AboutHelpAction()
        {
            super("mnuHelpAbout", HELP_ABOUT_COMMAND, Icons.About16);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class DefaultGeneratorsAction extends PCGenAction
    {

        private final Class<?> generatorClass;

        public DefaultGeneratorsAction(String prop, String command,
                                        Class<?> generatorClass)
        {
            super(prop, command);
            this.generatorClass = generatorClass;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class DefaultFiltersAction extends PCGenAction
    {

        private final Class<?> filterClass;

        public DefaultFiltersAction(String prop, String command,
                                     Class<?> filterClass)
        {
            super(prop, command);
            this.filterClass = filterClass;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
