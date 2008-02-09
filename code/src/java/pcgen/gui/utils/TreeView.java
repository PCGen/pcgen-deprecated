/*
 * TreeView.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on Feb 8, 2008, 8:29:38 PM
 */
package pcgen.gui.utils;

import java.util.Collection;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.gui.filter.Filter;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public interface TreeView<E extends PObject>
{

    public String getDisplayedName();

    public PObjectNode buildTree(Filter filter, PlayerCharacter pc, Collection<E> objs);

}
