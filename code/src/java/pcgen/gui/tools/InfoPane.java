/*
 * InfoPane.java
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
 * Created on Jun 26, 2008, 9:32:04 PM
 */
package pcgen.gui.tools;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class InfoPane extends JScrollPane
{

    private JTextPane textPane;
    private String title;

    public InfoPane()
    {
        this("Info");
    }

    public InfoPane(String title)
    {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
              JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setTitle(title);
        this.textPane = new JTextPane();
        this.setViewportView(textPane);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
        setBorder(BorderFactory.createTitledBorder(null, title,
                                                   TitledBorder.CENTER,
                                                   TitledBorder.DEFAULT_POSITION));
    }

}
