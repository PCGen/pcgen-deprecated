/*
 * SimpleTextIcon.java
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
 * Created on Jun 17, 2008, 4:24:42 PM
 */
package pcgen.gui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SimpleTextIcon implements Icon
{

    private final String text;
    private final FontMetrics metrics;
    private final Color color;

    public SimpleTextIcon(Component c, String text)
    {
        this(c, text, Color.BLACK);
    }

    public SimpleTextIcon(Component c, String text, Color color)
    {
        this.text = text;
        this.metrics = c.getFontMetrics(c.getFont());
        this.color = color;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        g.setColor(color);
        g.setFont(metrics.getFont());
        g.drawString(text, x, y + metrics.getAscent());
    }

    public int getIconWidth()
    {
        return metrics.stringWidth(text);
    }

    public int getIconHeight()
    {
        return metrics.getHeight();
    }

}
