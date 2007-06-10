/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.filter;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceFilter;
import pcgen.core.CDOMListObject;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class PCListFilter<T extends CDOMObject> implements ChoiceFilter<T>
{

	private HashMapToList<AssociationKey<?>, Object> assoc;

	private CDOMSimpleSingleRef<? extends CDOMListObject<T>> listRef;

	public static <T extends PObject> PCListFilter<T> getPCChooser(
		CDOMSimpleSingleRef<? extends CDOMListObject<T>> ref)
	{
		return new PCListFilter<T>(ref);
	}

	public PCListFilter(CDOMSimpleSingleRef<? extends CDOMListObject<T>> ref)
	{
		super();
		if (ref == null)
		{
			throw new IllegalArgumentException("Choice Class cannot be null");
		}
		listRef = ref;
	}

	public <A> void setAssociation(AssociationKey<A> ak, A val)
	{
		if (assoc == null)
		{
			assoc = new HashMapToList<AssociationKey<?>, Object>();
		}
		assoc.addToListFor(ak, val);
	}

	public boolean remove(PlayerCharacter pc, T obj)
	{
		CDOMListObject<T> listObj = listRef.resolvesTo();
		
		// TODO Auto-generated method stub
		return false;
	}

}
