/*
 * ChooserTab.java
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
 * Created on Jun 27, 2008, 12:53:57 PM
 */
package pcgen.gui.tabs;

import pcgen.gui.tools.*;
import java.awt.Component;
import java.util.Map;
import pcgen.gui.UIContext;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.util.panes.FlippingSplitPane;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class ChooserTab extends FlippingSplitPane implements CharacterInfoTab
{

    protected final UIContext context;
    protected CharacterFacade character;
    private FlippingSplitPane subSplitPane;
    private InfoPane infoPane;

    protected ChooserTab(UIContext context)
    {
        this.subSplitPane = new FlippingSplitPane(VERTICAL_SPLIT);
        this.infoPane = new InfoPane();
        this.context = context;

        subSplitPane.setTopComponent(infoPane);
        setRightComponent(subSplitPane);

        setDividerSize(7);
        setContinuousLayout(true);
        setOneTouchExpandable(true);
    }

    public void setPrimaryChooserComponent(Component c)
    {
        setLeftComponent(c);
    }

    public void setSecondaryChooserComponent(Component c)
    {
        subSplitPane.setBottomComponent(c);
    }

    public void setInfoPaneTitle(String title)
    {
        infoPane.setTitle(title);
    }

    public abstract Map<String, Object> saveModels();

    public abstract void loadModels(Map<String, Object> map);

    public void setCharacter(CharacterFacade character)
    {
        if (character != null && this.character != character)
        {
            if (this.character != null)
            {
                context.putUIData(this.character, getName(), saveModels());
            }
            this.character = character;
            loadModels(context.getUIData(character, getName()));
        }
    }

}
