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
package pcgen.cdom.choice;

import java.util.Set;

import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class AnyChooser<T extends PObject> extends AbstractChooser<T>
{

	private Class<T> choiceClass;

	private Category choiceCat;

	public static <T extends PObject> AnyChooser<T> getAnyChooser(Class<T> cl)
	{
		return new AnyChooser<T>(cl);
	}

	public AnyChooser(Class<T> cl)
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

	public <CT extends CategorizedCDOMObject<CT>> AnyChooser(Class<CT> cl,
		Category<CT> cat)
	{
		super();
		if (cl == null)
		{
			throw new IllegalArgumentException("Choice Class cannot be null");
		}
		if (cat == null)
		{
			throw new IllegalArgumentException("Choice Category cannot be null");
		}
		choiceClass = (Class<T>) cl;
		choiceCat = cat;
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		if (choiceCat == null)
		{
			return pc.getContext().ref.getConstructedCDOMObjects(choiceClass);
		}
		else
		{
			return pc.getContext().ref.getConstructedCDOMObjects(choiceClass,
				choiceCat);
		}
	}

	@Override
	public String toString()
	{
		return getCount().toString() + '<' + getMaxSelections().toString()
			+ Constants.PIPE + "Any: " + choiceClass;
	}

	@Override
	public int hashCode()
	{
		return chooserHashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof AnyChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		AnyChooser<?> cs = (AnyChooser) o;
		return equalsAbstractChooser(cs) && choiceClass.equals(cs.choiceClass);
	}

	public String getLSTformat()
	{
		return "ANY";
	}

	public Class<T> getChoiceClass()
	{
		return choiceClass;
	}

}
