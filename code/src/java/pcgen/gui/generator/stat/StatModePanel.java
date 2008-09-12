/*
 * StatModePanel.java
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
 * Created on Sep 10, 2008, 3:34:09 PM
 */
package pcgen.gui.generator.stat;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import pcgen.gui.generator.Generator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class StatModePanel<E extends Generator<Integer>> extends JPanel
{

    public StatModePanel()
    {
        super(new GridBagLayout());
    }

    public abstract void setGenerator(E generator);

    public abstract void saveGeneratorData();

}
