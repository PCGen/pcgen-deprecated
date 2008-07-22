/*
 * AbilitiesInfoTab.java
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
 * Created on Jul 15, 2008, 6:58:51 PM
 */
package pcgen.gui.tabs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pcgen.gui.PCGenUIManager;
import pcgen.gui.facade.AbilityCatagoryFacade;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.tabs.ability.AbilityChooserTab;
import pcgen.gui.util.DefaultGenericListModel;
import pcgen.gui.util.event.AbstractGenericListDataListener;
import pcgen.gui.util.event.GenericListDataEvent;
import pcgen.util.CollectionMaps;
import pcgen.util.ListMap;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class AbilitiesInfoTab extends JTabbedPane implements CharacterStateEditable
{

    private final AbilityChooserTab abilityTab;
    private Map<String, Hashtable<Object, Object>> tabStates = null;
    private String selectedTitle;

    public AbilitiesInfoTab()
    {
        this.abilityTab = new AbilityChooserTab();
        initComponents();
    }

    private void initComponents()
    {
        addChangeListener(
                new ChangeListener()
                {

                    public void stateChanged(ChangeEvent e)
                    {
                        abilityTab.storeState(tabStates.get(selectedTitle));
                        if (getSelectedIndex() != -1)
                        {
                            selectedTitle = getTitleAt(getSelectedIndex());
                            abilityTab.restoreState(tabStates.get(selectedTitle));
                        }
                    }

                });
    }

    public Hashtable<Object, Object> createState(final CharacterFacade character)
    {
        HashMap<String, Hashtable<Object, Object>> tabs = new HashMap<String, Hashtable<Object, Object>>();
        List<String> titles = new ArrayList<String>();
        @SuppressWarnings("unchecked")
        final ListMap<String, AbilityCatagoryFacade, DefaultGenericListModel<AbilityCatagoryFacade>> catagoryListMap =
                CollectionMaps.createListMap(HashMap.class,
                                             DefaultGenericListModel.class);

        final DefaultGenericListModel<AbilityCatagoryFacade> catagories =
                PCGenUIManager.getRegisteredAbilityCatagories(character);

        for (AbilityCatagoryFacade catagory : catagories)
        {
            catagoryListMap.add(catagory.getType(), catagory);
        }

        for (String type : catagoryListMap.keySet())
        {
            titles.add(type);
            tabs.put(type, abilityTab.createState(character,
                                                  catagoryListMap.get(type)));
        }
        catagories.addGenericListDataListener(
                new AbstractGenericListDataListener<AbilityCatagoryFacade>()
                {

                    public void intervalAdded(GenericListDataEvent<AbilityCatagoryFacade> e)
                    {
                        List<AbilityCatagoryFacade> subList = catagories.subList(e.getIndex0(),
                                                                                 e.getIndex1() +
                                                                                 1);
                        @SuppressWarnings("unchecked")
                        ListMap<String, AbilityCatagoryFacade, ArrayList<AbilityCatagoryFacade>> catagorySubListMap =
                                CollectionMaps.createListMap(HashMap.class,
                                                             ArrayList.class);
                        for ( AbilityCatagoryFacade catagory : subList)
                        {
                            catagorySubListMap.add(catagory.getType(), catagory);
                        }
                        // Warning this does not take into acount whether or not a new "type"
                        // has been introduced!!
                        for ( String type : catagorySubListMap.keySet())
                        {
                            catagoryListMap.addAll(type,
                                                   catagorySubListMap.get(type));
                        }
                    }

                    public void intervalRemoved(GenericListDataEvent<AbilityCatagoryFacade> e)
                    {
                        Collection<? extends AbilityCatagoryFacade> data = e.getData();
                        @SuppressWarnings("unchecked")
                        ListMap<String, AbilityCatagoryFacade, ArrayList<AbilityCatagoryFacade>> catagorySubListMap =
                                CollectionMaps.createListMap(HashMap.class,
                                                             ArrayList.class);
                        for ( AbilityCatagoryFacade catagory : data)
                        {
                            catagorySubListMap.add(catagory.getType(), catagory);
                        }
                        // Warning this does not take into acount whether or not a "type"
                        // should be removed from the tabs
                        for ( String type : catagorySubListMap.keySet())
                        {
                            catagoryListMap.removeAll(type,
                                                      catagorySubListMap.get(type));
                        }
                    }

                });
        Hashtable<Object, Object> state = new Hashtable<Object, Object>();
        state.put("Titles", titles);
        state.put("Tabs", tabs);
        state.put("SelectedTitle", titles.get(0));
        return state;
    }

    public void storeState(Hashtable<Object, Object> state)
    {
        state.put("SelectedTitle", selectedTitle);
    }

    @SuppressWarnings("unchecked")
    public void restoreState(Hashtable<?, ?> state)
    {
        removeAll();
        List<String> titles = (List<String>) state.get("Titles");
        for (String title : titles)
        {
            addTab(title, abilityTab);
        }
        tabStates = (Map<String, Hashtable<Object, Object>>) state.get("Tabs");
        selectedTitle = (String) state.get("SelectedTitle");
        setSelectedIndex(indexOfTab(selectedTitle));
    }

}
