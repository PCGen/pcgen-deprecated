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
package pcgen.gui.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public class ResourceManager
{

    /** The URL to the resource folder of pcgen */
    public static final String RESOURCE_URL = "/pcgen/gui/resource/";
    private static final Map<Icon, ImageIcon> iconMap = new HashMap<Icon, ImageIcon>();

    public static enum Icon
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

    private ResourceManager()
    {
    }

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

    public static ImageIcon getImageIcon(Icon icon)
    {
        ImageIcon image = iconMap.get(icon);
        if (image == null)
        {
            image = getImageIcon(icon.toString() + ".gif");
            iconMap.put(icon, image);
        }
        return image;
    }

}
