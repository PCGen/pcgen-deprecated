/*
 * DataSetFacade.java
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
 * Created on Aug 19, 2008, 3:31:30 PM
 */
package pcgen.gui.facade;

import java.io.File;
import java.util.Set;
import pcgen.gui.util.GenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface DataSetFacade
{

    public GenericListModel<AbilityFacade> getAbilities(AbilityCatagoryFacade catagory);

    public AbilityFacade getAbility(AbilityCatagoryFacade catagory,
                                     String ability);

    public GenericListModel<AbilityCatagoryFacade> getAbilityCatagories();

    public AbilityCatagoryFacade getAbilityCatagory(String catagory);

    public GenericListModel<SkillFacade> getSkills();

    public SkillFacade getSkill(String skill);

    public GenericListModel<RaceFacade> getRaces();

    public RaceFacade getRace(String race);

    public GenericListModel<ClassFacade> getClasses();

    public ClassFacade getClass(String className);

    public GenericListModel<TempBonusFacade> getTempBonuses();

    public GenericListModel<TemplateFacade> getTemplates();

    public Set<String> getSources();

    public Set<File> getGeneratorFiles();

    public GameModeFacade getGameMode();

}
