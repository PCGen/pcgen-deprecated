/*
 * DefaultMutableAssignmentModeGenerator.java
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
 * Created on Sep 10, 2008, 5:26:13 PM
 */
package pcgen.gui.generator.stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DefaultMutableAssignmentModeGenerator extends DefaultAssignmentModeGenerator
        implements MutableAssignmentModeGenerator
{

    public DefaultMutableAssignmentModeGenerator(String name)
    {
        super(name);
    }

    public void setScores(List<Integer> scores)
    {
        this.scores = scores;
        this.temp = new ArrayList<Integer>(scores);
        reset();
    }

    public void setAssignable(boolean assignable)
    {
        this.assignable = assignable;
    }

}
