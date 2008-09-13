/*
 * AbilityBuildSelectionDialog.java
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
 * Created on Sep 12, 2008, 1:35:37 PM
 */
package pcgen.gui.generator.ability;

import java.awt.Component;
import pcgen.gui.tools.AbstractSelectionDialog;
import pcgen.gui.tools.ResourceManager;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class AbilityBuildSelectionDialog extends AbstractSelectionDialog<AbilityBuild>
{

    private AbilityBuildPanel abilityPanel;

    public AbilityBuildSelectionDialog()
    {
        super(ResourceManager.getText("availAbilBuild"),
              ResourceManager.getText("selAbilBuild"),
              ResourceManager.getToolTip("newAbilBuild"),
              ResourceManager.getToolTip("copyAbilBuild"),
              ResourceManager.getToolTip("deleteAbilBuild"),
              ResourceManager.getToolTip("addAbilBuild"),
              ResourceManager.getToolTip("removeAbilBuild"));
    }

    @Override
    protected void initComponents()
    {
        this.abilityPanel = new AbilityBuildPanel();
    }

    @Override
    protected Component getLeftComponent()
    {
        return abilityPanel;
    }

    @Override
    protected AbilityBuild createMutableItem(AbilityBuild item)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean isMutable(Object item)
    {
        return item instanceof MutableAbilityBuild;
    }

}
