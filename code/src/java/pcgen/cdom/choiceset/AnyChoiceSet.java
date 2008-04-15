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
package pcgen.cdom.choiceset;

import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.character.CharacterDataStore;

public class AnyChoiceSet<T extends CDOMObject> implements
		PrimitiveChoiceSet<T>
{

	private Class<T> choiceClass;

	public static <T extends CDOMObject> AnyChoiceSet<T> getAnyChooser(
			Class<T> cl)
	{
		return new AnyChoiceSet<T>(cl);
	}

	public AnyChoiceSet(Class<T> cl)
	{
		super();
		if (cl == null)
		{
			throw new IllegalArgumentException("Choice Class cannot be null");
		}
		if (CategorizedCDOMObject.class.isAssignableFrom(cl))
		{
			throw new IllegalArgumentException(
					"Cannot use Categorized Class without a Category");
		}
		choiceClass = cl;
	}

	public Set<T> getSet(CharacterDataStore pc)
	{
		return pc.getRulesData().getAll(choiceClass);
	}

	public String getLSTformat()
	{
		return "ANY";
	}

	public Class<T> getChoiceClass()
	{
		return choiceClass;
	}

	@Override
	public int hashCode()
	{
		return choiceClass.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof AnyChoiceSet
				&& choiceClass.equals(((AnyChoiceSet<?>) o).choiceClass);
	}
}
