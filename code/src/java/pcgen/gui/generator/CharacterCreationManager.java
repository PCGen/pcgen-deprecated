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
package pcgen.gui.generator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom.DocType;
import org.jdom.Document;
import pcgen.cdom.enumeration.Gender;
import pcgen.gui.facade.ClassFacade;
import pcgen.gui.facade.DataSetFacade;
import pcgen.gui.facade.RaceFacade;
import pcgen.gui.facade.SkillFacade;
import pcgen.gui.facade.StatFacade;
import pcgen.gui.generator.Generator;
import pcgen.gui.util.GenericComboBoxModel;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CharacterCreationManager
{

    public static final String NAME_VALIDITY = "Name";
    public static final String ALIGNMENT_VALIDITY = "Alignment";
    public static final String GENDER_VALIDITY = "Gender";
    public static final String RACE_VALIDITY = "Race";
    public static final String STATS_VALIDITY = "Stats";
    public static final String CLASSES_VALIDITY = "Classes";
    private final PropertyChangeSupport support;
    private final Map<String, Boolean> validityMap;

    public CharacterCreationManager(DataSetFacade data)
    {
        support = new PropertyChangeSupport(this);
        validityMap = new HashMap<String, Boolean>();
        List<Generator<Integer>> statGenerators = new ArrayList<Generator<Integer>>();
        List<Generator<SkillFacade>> skillGenerators = new ArrayList<Generator<SkillFacade>>();
        Set<File> files = data.getGeneratorFiles();
        for (File file : files)
        {
            try
            {
                Document document = GeneratorFactory.buildDocument(file);
                DocType type = document.getDocType();
                if (type.getElementName().equals("GENERATORSET"))
                {
                    String systemid = type.getSystemID();
                    if (systemid.equals("StandardModeGenerator.dtd"))
                    {
                        statGenerators.addAll(GeneratorFactory.buildStandardModeGeneratorList(document));
                    }
                    else if (systemid.equals("PurchaseModeGenerator.dtd"))
                    {
                        statGenerators.addAll(GeneratorFactory.buildPurchaseModeGeneratorList(document));
                    }
                    else if (systemid.equals("SkillGenerator.dtd"))
                    {
                        skillGenerators.addAll(GeneratorFactory.buildSkillGeneratorList(document,
                                                                                        data));
                    }
                }
            }
            catch (Exception ex)
            {
                Logging.errorPrint(ex.getMessage(), ex);
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        support.addPropertyChangeListener(l);
    }

    public void addPropertyChangeListener(String prop,
                                           PropertyChangeListener l)
    {
        support.addPropertyChangeListener(prop, l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        support.removePropertyChangeListener(l);
    }

    public void removePropertyChangeListener(String prop,
                                              PropertyChangeListener l)
    {
        support.removePropertyChangeListener(prop, l);
    }

    public boolean isCharacterValid()
    {
        return !validityMap.values().contains(Boolean.FALSE);
    }

    public boolean isCharacterNameValid()
    {
        return validityMap.get(NAME_VALIDITY);
    }

    public void setValidity(String prop, boolean valid)
    {
        boolean oldvalue = validityMap.get(prop);
        validityMap.put(prop, valid);
        support.firePropertyChange(prop, oldvalue, valid);
    }

    public GenericComboBoxModel<Generator<Integer>> getAlignmentGenerators()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericComboBoxModel<Generator<Gender>> getGenderGenerators()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericComboBoxModel<Generator<RaceFacade>> getRaceGenerators()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericComboBoxModel<Generator<ClassFacade>> getClassGenerators()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericComboBoxModel<Generator<Integer>> getStatGenerators()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<StatFacade> getStats()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getModForScore(int score)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericComboBoxModel<Generator<Integer>> getClassLevelGenerators()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
