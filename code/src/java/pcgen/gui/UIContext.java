/*
 * UIContext.java
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
 * Created on Jun 17, 2008, 3:52:41 PM
 */
package pcgen.gui;

import java.util.HashMap;
import java.util.Map;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.filter.NamedFilter;
import pcgen.gui.util.GenericListModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class UIContext
{
    //public static final String CLASS_TAB_ID = "classTab";
    private final Map<CharacterFacade, Map<String, Map<String, Object>>> dataMap = new HashMap<CharacterFacade, Map<String, Map<String, Object>>>();

    public <T> GenericListModel<NamedFilter<? super T>> getRegisteredFilters(Class<T> c)
    {
        return null;
    }

    public Map<String, Object> getUIData(CharacterFacade facade,
                                          String dataid)
    {
        return dataMap.get(facade).get(dataid);
    }

    public void putUIData(CharacterFacade facade, String dataid,
                           Map<String, Object> map)
    {
        dataMap.get(facade).put(dataid, map);
    }

}
