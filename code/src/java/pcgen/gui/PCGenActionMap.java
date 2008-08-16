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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import pcgen.gui.util.ResourceManager;
import pcgen.gui.util.ResourceManager.Icons;
import pcgen.util.PropertyFactory;

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
    public static final String TOOLS_COMMAND = "tools";
    public static final String SOURCES_COMMAND = TOOLS_COMMAND + ".sources";
    public static final String SOURCES_ADVANCED_COMMAND = SOURCES_COMMAND +
            ".advanced";
    public static final String FILTERS_COMMAND = TOOLS_COMMAND + ".filters";
    public static final String GENERATORS_COMMAND = TOOLS_COMMAND +
            ".generators";
    public static final String OPTIONS_COMMAND = TOOLS_COMMAND + ".options";
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

        put(TOOLS_COMMAND, new ToolsAction());
        put(SOURCES_COMMAND, new SourcesAction());
        put(SOURCES_ADVANCED_COMMAND, new AdvancedSourcesAction());
        put(FILTERS_COMMAND, new FiltersAction());
        put(GENERATORS_COMMAND, new GeneratorsAction());
        put(OPTIONS_COMMAND, new OptionsAction());
    }

    private class FileAction extends PCGenAction
    {

        public FileAction()
        {
            super("mnuFile");
        }

        public void actionPerformed(ActionEvent e)
        {

        }

    }

    private class NewAction extends PCGenAction
    {

        public NewAction()
        {
            super("mnuFileNew", NEW_COMMAND, "shortcut N", Icons.New16);
        }

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

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class CloseAction extends PCGenAction
    {

        public CloseAction()
        {
            super("mnuFileClose", CLOSE_COMMAND, "shortcut W", Icons.Close16);
        }

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

        public void actionPerformed(ActionEvent e)
        {

        }

    }

    private class OpenPartyAction extends PCGenAction
    {

        public OpenPartyAction()
        {
            super("mnuFilePartyOpen", OPEN_PARTY_COMMAND, Icons.Open16);
        }

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

        public void actionPerformed(ActionEvent e)
        {

        }

    }

    private class ExportStandardAction extends PCGenAction
    {

        public ExportStandardAction()
        {
            super("mnuFileExportStandard", EXPORT_STANDARD_COMMAND);
        }

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

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class ToolsAction extends PCGenAction
    {

        public ToolsAction()
        {
            super("mnuTools");
        }

        public void actionPerformed(ActionEvent e)
        {

        }

    }

    private class SourcesAction extends PCGenAction
    {

        public SourcesAction()
        {
            super(null);
        }

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

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class FiltersAction extends PCGenAction
    {

        public FiltersAction()
        {
            super("mnuToolsFilters");
        }

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

        public void actionPerformed(ActionEvent e)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private static abstract class PCGenAction extends AbstractAction
    {

        public PCGenAction(String prop)
        {
            this(prop, null, null, null);
        }

        public PCGenAction(String prop, Icons icon)
        {
            this(prop, null, null, icon);
        }

        public PCGenAction(String prop, String command)
        {
            this(prop, command, null, null);
        }

        public PCGenAction(String prop, String command, Icons icon)
        {
            this(prop, command, null, icon);
        }

        public PCGenAction(String prop, String command, String accelerator)
        {
            this(prop, command, accelerator, null);
        }

        public PCGenAction(String prop, String command, String accelerator,
                            Icons icon)
        {
            putValue(NAME,
                     PropertyFactory.getString("in_" + prop));
            putValue(MNEMONIC_KEY,
                     PropertyFactory.getMnemonic("in_mn_" + prop));
            putValue(SHORT_DESCRIPTION,
                     PropertyFactory.getString("in_" + prop + "Tip"));

            if (command != null)
            {
                putValue(ACTION_COMMAND_KEY, command);
            }
            if (accelerator != null)
            {
                // accelerator has three possible forms:
                // 1) shortcut +
                // 2) shortcut-alt +
                // 3) F1
                // (error checking is for the weak!)
                int iShortCut = KeyEvent.CTRL_MASK;
                int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
                StringTokenizer aTok = new StringTokenizer(accelerator);

                // get the first argument
                String aString = aTok.nextToken();

                if (aString.equalsIgnoreCase("shortcut"))
                {
                    iShortCut = menuShortcutKeyMask;
                }
                else if (aString.equalsIgnoreCase("alt"))
                {
                    if (System.getProperty("mrj.version") != null)
                    {
                        iShortCut = menuShortcutKeyMask | KeyEvent.ALT_MASK;
                    }
                    else
                    {
                        iShortCut = KeyEvent.ALT_MASK;
                    }
                }
                else if (aString.equalsIgnoreCase("shift-shortcut"))
                {
                    iShortCut = menuShortcutKeyMask | KeyEvent.SHIFT_MASK;
                }

                if (aTok.hasMoreTokens())
                {
                    // get the second argument
                    aString = aTok.nextToken();
                }

                KeyStroke aKey = KeyStroke.getKeyStroke(aString);

                if (aKey != null)
                {
                    int iKeyCode = aKey.getKeyCode();
                    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(iKeyCode,
                                                                     iShortCut));
                }
            }
            if (icon != null)
            {
                putValue(SMALL_ICON, ResourceManager.getImageIcon(icon));
            }
        }

    }
}
