/*
 * MutableCharacterBuild.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jan 27, 2009, 4:34:47 PM
 */
package pcgen.gui.generator;

import pcgen.cdom.enumeration.Gender;
import pcgen.gui.facade.AlignmentFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.generator.ability.AbilityBuild;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface MutableCharacterBuild extends CharacterBuild, Mutable
{

    public void setAlignmentGenerator(Generator<AlignmentFacade> generator);

    public void setGenderGenerator(Generator<Gender> generator);

    public void setStatGenerator(Generator<Integer> generator);

    public void setRaceGenerator(InfoFacadeGenerator<RaceFacade> generator);

    public void setClassGenerator(InfoFacadeGenerator<ClassFacade> generator);

    public void setSkillGenerator(InfoFacadeGenerator<SkillFacade> generator);

    public void setAbilityBuild(AbilityBuild build);

}
