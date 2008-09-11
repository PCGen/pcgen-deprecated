/*
 * DefaultAssignmentModeGenerator.java
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
 * Created on Sep 10, 2008, 5:45:27 PM
 */
package pcgen.gui.generator.stat;

import java.util.Collections;
import java.util.List;
import pcgen.gui.generator.AbstractGenerator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DefaultAssignmentModeGenerator extends AbstractGenerator<Integer>
        implements AssignmentModeGenerator
{

    protected List<Integer> scores;
    protected List<Integer> temp;
    protected boolean assignable;
    private int index = 0;

    protected DefaultAssignmentModeGenerator(String name)
    {
        super(name);
        this.scores = Collections.emptyList();
        this.temp = Collections.emptyList();
        this.assignable = false;
    }

    public DefaultAssignmentModeGenerator(String name, List<Integer> scores,
                                           List<Integer> temp,
                                           boolean assignable)
    {
        super(name);
        this.scores = scores;
        this.temp = temp;
        this.assignable = assignable;
    }

    public boolean isAssignable()
    {
        return assignable;
    }

    public Integer getNext()
    {

        if (temp.isEmpty())
        {
            return null;
        }
        if (index == temp.size())
        {
            reset();
        }
        return temp.get(index++);
    }

    @Override
    public void reset()
    {
        Collections.shuffle(temp);
        index = 0;
    }

    @Override
    public List<Integer> getAll()
    {
        return scores;
    }

}
