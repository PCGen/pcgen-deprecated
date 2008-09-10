/*
 * ResourceManager.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on Feb 24, 2008, 9:52:23 PM
 */
package pcgen.gui.tools;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import pcgen.base.lang.Internationalization;
import pcgen.core.Constants;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public final class ResourceManager
{

    private ResourceManager()
    {
    }

    public static final String TOOLS_BUNDLE = "pcgen/gui/tools/ToolsBundle";
    public static final String LANGUATE_BUNDLE = "pcgen/gui/prop/LanguageBundle";
    public static final String LANGUATE_BUNDLE2 = "pcgen/gui/prop/LanguageBundle2";
    public static final String GENERATOR_BUNDLE = "pcgen/gui/generator/GeneratorBundle";
    /** Undefined Property */
    public static final String UNDEFINED = " not defined."; //$NON-NLS-1$
    /** The URL to the resource folder of pcgen */
    public static final String RESOURCE_URL = "/pcgen/gui/resource/";
    private static final Map<String, ResourceBundle> bundleMap = new HashMap<String, ResourceBundle>();
    private static final Map<Icons, ImageIcon> iconMap = new HashMap<Icons, ImageIcon>();

    static
    {
        Locale locale = null;
        String language = Internationalization.getLanguage();
        if (language == null || language.equals(Constants.EMPTY_STRING))
        {
            locale = Locale.getDefault();
        }
        else
        {
            locale = new Locale(Internationalization.getLanguage(),
                                Internationalization.getCountry());
            // We reset the default so that
            // a) The dialog buttons match the selected language.
            // b) English (if selected) isn't overriden by the system default
            Locale.setDefault(locale);
        }
        ensureLoaded(LANGUATE_BUNDLE2);
    }

    /**
     * This method exists as a temporary handler for multiple ResourceBundles
     * This is due to be removed once all necessary bundles are joined into one.
     * @param resourceBundle
     */
    public static void ensureLoaded(String resourceBundle)
    {
        if (bundleMap.containsKey(resourceBundle))
        {
            return;
        }
        try
        {
            bundleMap.put(resourceBundle,
                          ResourceBundle.getBundle(resourceBundle,
                                                   Locale.getDefault()));
        }
        catch (MissingResourceException mrex)
        {
            Logging.errorPrint("Can't find language bundle: " + resourceBundle,
                               mrex); //$NON-NLS-1$
        }
    }

    /**
     * Fetch an <code>ImageIcon</code> relative to the calling
     * location.
     *
     * @param location <code>String</code>, the path to the
     * <code>IconImage> source
     *
     * @return <code>ImageIcon</code>, the icon or <code>null</code>
     * on failure
     */
    public static ImageIcon getImageIcon(String fileName)
    {
        fileName = RESOURCE_URL + fileName;
        final URL iconURL = ResourceManager.class.getResource(fileName);
        if (iconURL == null)
        {
            return null;
        }
        return new ImageIcon(iconURL);
    }

    public static ImageIcon getImageIcon(Icons icon)
    {
        ImageIcon image = iconMap.get(icon);
        if (image == null)
        {
            image = getImageIcon(icon.toString() + ".gif");
            iconMap.put(icon, image);
        }
        return image;
    }

    /**
     * 
     * @param key
     * @return
     * @throws MissingResourceException thrown if the key does not exist in any of the
     * loaded bundles
     */
    public static String getString(String key)
    {
        for (ResourceBundle bundle : bundleMap.values())
        {
            try
            {
                return bundle.getString(key);
            }
            catch (MissingResourceException mrex)
            {
            }
        }
        MissingResourceException e = new MissingResourceException("Can't find resource for key " +
                                                                  key,
                                                                  ResourceManager.class.getName(),
                                                                  key);
        Logging.errorPrint("Resource not found", e); //$NON-NLS-1$
        throw e;
    }

    /**
     * Note: This method automatically adds "in_" to be beginning of the 
     * <code>prop</code> parameter
     * @param prop
     * @return
     */
    public static String getText(String prop)
    {
        String value = "in_" + prop;
        try
        {
            value = getString(value);
        }
        catch (MissingResourceException mre)
        {
            value = value + UNDEFINED;
        }
        return value;
    }

    /**
     * Node: This method automatically adds "Tip" to the end of the 
     * <code>prop</code> parameter
     * @param prop
     * @return
     */
    public static String getToolTip(String prop)
    {
        return getText(prop + "Tip");
    }

    /**
     * Note: This method automatically adds "in_mn_" to be beginning of the 
     * <code>prop</code> parameter
     * @param prop
     * @return
     */
    public static int getMnemonic(String prop)
    {
        String value = "in_mn_" + prop;
        try
        {
            value = getString(value);
            if (value.length() > 0)
            {
                return value.charAt(0);
            }
        }
        catch (MissingResourceException mre)
        {

        }
        return 0;
    }

    public static enum Icons
    {

        About16,
        Add16,
        AlignBottom16,
        AlignCenter16,
        AlignJustifyHorizontal16,
        AlignJustifyVertical16,
        AlignLeft16,
        AlignRight16,
        AlignTop16,
        BBack16,
        BBack24,
        Back16,
        Bookmarks16,
        Checklist16,
        Close16,
        CloseAll16,
        ComposeMail16,
        ContextualHelp16,
        Copy16,
        CustomZoom16,
        Cut16,
        DDown16,
        DDown24,
        DefaultPortrait,
        Delete16,
        Down16,
        Edit16,
        EditZoom16,
        Export16,
        FForward16,
        FForward24,
        Find16,
        FindAgain16,
        Forward16,
        Help16,
        History16,
        Import16,
        Information16,
        MediaStop16,
        New16,
        NewEnvelope,
        NewNPC16,
        Open16,
        PageSetup16,
        Paste16,
        PcgenIcon,
        Preferences16,
        PreferencesHighlightBlue16,
        Print16,
        PrintPreview16,
        Properties16,
        Redo16,
        Refresh16,
        Remove16,
        RemovePreferences16,
        RemoveZoom16,
        Replace16,
        Save16,
        SaveAll16,
        SaveAs16,
        Search16,
        SendMail16,
        SplashPcgen,
        Stop16,
        TipOfTheDay16,
        TipOfTheDay24,
        UUp16,
        UUp24,
        Undo16,
        Up16,
        Zoom16,
        ZoomHighlightBlue16,
        ZoomIn16,
        ZoomOut16;
    }
}
