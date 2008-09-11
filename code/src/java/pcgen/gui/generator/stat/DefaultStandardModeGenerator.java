/*
 * DefaultStandardModeGenerator.java
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
 * Created on Sep 10, 2008, 6:05:27 PM
 */
package pcgen.gui.generator.stat;

import java.util.Collections;
import java.util.List;
import pcgen.gui.generator.AbstractGenerator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DefaultStandardModeGenerator extends AbstractGenerator<Integer>
        implements StandardModeGenerator
{

    protected String diceExpression;
    protected int dropCount;
    protected int rerollMinimum;

    public DefaultStandardModeGenerator(String name, String diceExpression,
                                         int dropCount,
                                         int rerollMinimum)
    {
        super(name);
        this.diceExpression = diceExpression;
        this.dropCount = dropCount;
        this.rerollMinimum = rerollMinimum;
    }

    public Integer getNext()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Integer> getAll()
    {
        return Collections.emptyList();
    }

    public String getDiceExpression()
    {
        return diceExpression;
    }

    public int getRerollMinimum()
    {
        return rerollMinimum;
    }

    public int getDropCount()
    {
        return dropCount;
    }

}
