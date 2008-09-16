/*
 * SkillGenerator.java
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
 * Created on Sep 13, 2008, 7:24:08 PM
 */
package pcgen.gui.generator.skill;

import pcgen.gui.facade.SkillFacade;
import pcgen.gui.generator.OrderedGenerator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface SkillGenerator extends OrderedGenerator<SkillFacade>
{

    public int getSkillPriority(SkillFacade skill);

}
