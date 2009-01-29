/*
 * CharacterBuild.java
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
 * Created on Jan 18, 2009, 8:13:08 PM
 */
package pcgen.gui.generator;

import pcgen.cdom.enumeration.Gender;
import pcgen.gui.facade.AlignmentFacade;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.facade.StatFacade;
import pcgen.gui.generator.ability.AbilityBuild;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface CharacterBuild
{

    public Generator<AlignmentFacade> getAlignmentGenerator();

    public Generator<Gender> getGenderGenerator();

    public Generator<Integer> getStatGenerator();

    public InfoFacadeGenerator<RaceFacade> getRaceGenerator();

    public InfoFacadeGenerator<ClassFacade> getClassGenerator();

    public InfoFacadeGenerator<SkillFacade> getSkillGenerator();

    public AbilityBuild getAbilityBuild();

    public GeneratorManager getGeneratorManager();

}
