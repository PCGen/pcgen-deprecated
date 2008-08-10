/*
 * CharacterCreationManager.java
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
 * Created on Aug 8, 2008, 3:18:20 PM
 */
package pcgen.gui;

import java.beans.PropertyChangeListener;
import java.util.List;
import pcgen.cdom.enumeration.Gender;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.StatFacade;
import pcgen.gui.generator.Generator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface CharacterCreationManager
{

    public static final String NAME_VALIDITY = "Name";
    public static final String ALIGNMENT_VALIDITY = "Alignment";
    public static final String GENDER_VALIDITY = "Gender";
    public static final String RACE_VALIDITY = "Race";
    public static final String STATS_VALIDITY = "Stats";
    public static final String CLASSES_VALIDITY = "Classes";

    public void addPropertyChangeListener(PropertyChangeListener l);

    public void addPropertyChangeListener(String prop, PropertyChangeListener l);

    public void removePropertyChangeListener(PropertyChangeListener l);

    public void removePropertyChangeListener(String prop,
                                              PropertyChangeListener l);

    public boolean isCharacterValid();

    public boolean isCharacterNameValid();

    public void setCharacterNameValidity(boolean b);

    public void setValidity(String prop, boolean valid);

    public List<Generator<Integer>> getAlignmentGenerators();

    public List<Generator<Gender>> getGenderGenerators();

    public List<Generator<RaceFacade>> getRaceGenerators();

    public List<Generator<ClassFacade>> getClassGenerators();

    public List<Generator<Integer>> getStatGenerators();

    public List<StatFacade> getStats();

    public int getModForScore(int score);

    public List<Generator<Integer>> getClassLevelGenerators();

}
