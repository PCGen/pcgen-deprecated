/*
 * CharacterSheetPanel.java
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
 * Created on Aug 19, 2008, 3:06:38 PM
 */
package pcgen.gui;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.w3c.dom.Document;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.tools.CharacterSelectionListener;
import pcgen.gui.util.SwingWorker;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CharacterSheetPanel extends HtmlPanel implements CharacterSelectionListener
{

    /** BLUE = 0 */
    public static final int BLUE = 0;
    /** LIGHTBLUE = 1 */
    public static final int LIGHTBLUE = 1;
    /** GREEN = 2 */
    public static final int GREEN = 2;
    /** LIGHTGREEN = 3 */
    public static final int LIGHTGREEN = 3;
    /** RED = 4 */
    public static final int RED = 4;
    /** LIGHTRED = 5 */
    public static final int LIGHTRED = 5;
    /** YELLOW = 6 */
    public static final int YELLOW = 6;
    /** LIGHTYELLOW = 7 */
    public static final int LIGHTYELLOW = 7;
    /** GREY = 8 */
    public static final int GREY = 8;
    /** LIGHTGREY = 9 */
    public static final int LIGHTGREY = 9;
    private final HtmlRendererContext theRendererContext;
    private final DocumentBuilderImpl theDocBuilder;
    private ExportHandler handler = null;
    private CharacterFacade character = null;
    private RefreshWorker worker = null;

    public CharacterSheetPanel()
    {
        theRendererContext = new SimpleHtmlRendererContext(this);
        theDocBuilder = new DocumentBuilderImpl(theRendererContext.getUserAgentContext(),
                                                theRendererContext);
    }

    public void setCharacter(CharacterFacade character)
    {
        this.character = character;
        refresh();
    }

    public void setCharacterSheet(File sheet)
    {
        handler = new ExportHandler(sheet);
        refresh();
    }

    private void refresh()
    {
        if (handler == null || character == null)
        {
            return;
        }
        if (worker != null)
        {
            worker.interrupt();
        }
        worker = new RefreshWorker();
        worker.start();
    }

    private class RefreshWorker extends SwingWorker<Document>
    {

        private String getColorCSS()
        {
            int value = 0;// TODO Implement
            //SettingsHandler.getGMGenOption(CharacterSheetPlugin.LOG_NAME
            //	+ ".color", CharacterPanel.BLUE);
            switch (value)
            {
                case BLUE:
                    return "preview_color_blue.css";
                case LIGHTBLUE:
                    return "preview_color_light_blue.css";
                case GREEN:
                    return "preview_color_green.css";
                case LIGHTGREEN:
                    return "preview_color_light_green.css";
                case RED:
                    return "preview_color_red.css";
                case LIGHTRED:
                    return "preview_color_light_red.css";
                case YELLOW:
                    return "preview_color_yellow.css";
                case LIGHTYELLOW:
                    return "preview_color_light_yellow.css";
                case GREY:
                    return "preview_color_grey.css";
                case LIGHTGREY:
                    return "preview_color_light_grey.css";
                default:
                    return "preview_color_blue.css";
            }
        }

        @Override
        public Document construct()
        {
            StringWriter out = new StringWriter();
            BufferedWriter buf = new BufferedWriter(out);
            handler.write(character, buf);
            Thread t = Thread.currentThread();
            if (t.isInterrupted())
            {
                return null;
            }
            String genText = out.toString().replace("preview_color.css",
                                                    getColorCSS());
            ByteArrayInputStream instream = new ByteArrayInputStream(genText.getBytes());
            Document doc = null;
            try
            {
                final URI root = new URI("file",
                                          PCGenUIManager.getPcgenPreviewDir().getAbsolutePath().replaceAll("\\\\",
                                                                                                           "/"),
                                          null);

                doc = theDocBuilder.parse(new InputSourceImpl(instream,
                                                              root.toString(),
                                                              "UTF-8"));
            }
            catch (Throwable e)
            {
                // TODO Auto-generated catch block
                final String errorMsg = "<html><body>Unable to process sheet<br>" +
                        e + "</body></html>";
                instream = new ByteArrayInputStream(errorMsg.getBytes());
                try
                {
                    doc = theDocBuilder.parse(instream);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                Logging.errorPrint("Unable to process sheet: ", e);
            }
            finally
            {
                return doc;
            }
        }

        @Override
        public void finished()
        {
            Document doc = get();
            if (doc != null)
            {
                setDocument(doc, theRendererContext);
            }
        }

    }
}
