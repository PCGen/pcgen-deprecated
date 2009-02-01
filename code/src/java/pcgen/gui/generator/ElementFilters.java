/*
 * ElementFilters.java
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
 * Created on Jan 31, 2009, 12:16:09 AM
 */
package pcgen.gui.generator;

import org.jdom.Element;
import org.jdom.filter.AbstractFilter;
import org.jdom.filter.Filter;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class ElementFilters
{

    private static Filter refFilter = new Filter()
    {

        public boolean matches(Object obj)
        {
            if (obj instanceof Element)
            {
                Element element = (Element) obj;
                if (element.getName().endsWith("_REF"))
                {
                    return true;
                }
            }
            return false;
        }

    };
    private static AbstractFilter buildFilter = new AbstractFilter()
    {

        public boolean matches(Object obj)
        {
            if (obj instanceof Element)
            {
                Element element = (Element) obj;
                return element.getName().matches("BUILD");
            }
            return false;
        }

    };

    public static Filter getReferenceFilter()
    {
        return refFilter;
    }

    public static AbstractFilter getBuildFilter()
    {
        return buildFilter;
    }

}
