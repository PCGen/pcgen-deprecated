/*
 * DefaultPurchaseModeGenerator.java
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
 * Created on Sep 10, 2008, 6:13:23 PM
 */
package pcgen.gui.generator.stat;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import pcgen.gui.generator.AbstractGenerator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DefaultPurchaseModeGenerator extends AbstractGenerator<Integer>
        implements PurchaseModeGenerator
{

    protected Vector<Integer> costs;
    protected int points;
    protected int min;

    protected DefaultPurchaseModeGenerator(String name)
    {
        super(name);
        this.points = 0;
        this.min = 0;
        this.costs = new Vector<Integer>();
    }

    public DefaultPurchaseModeGenerator(String name, int points, int min,
                                         List<Integer> costs)
    {
        super(name);
        this.points = points;
        this.min = min;
        this.costs = new Vector<Integer>(costs);
    }

    public Integer getNext()
    {
        return points;
    }

    public int getMinScore()
    {
        return min;
    }

    public int getMaxScore()
    {
        return min + costs.size() - 1;
    }

    public int getScoreCost(int score)
    {
        return costs.get(score - min);
    }

}
