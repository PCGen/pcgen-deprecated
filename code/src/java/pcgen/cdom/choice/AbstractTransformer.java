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

import pcgen.base.formula.Formula;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public abstract class AbstractTransformer<T extends PObject> implements
		ChoiceSet<T>
{

	private ChoiceSet<? extends PObject> choiceSet;

	public AbstractTransformer(ChoiceSet<? extends PObject> cs)
	{
		super();
		if (cs == null)
		{
			throw new IllegalArgumentException("Choice Set cannot be null");
		}
		choiceSet = cs;
	}

	public Formula getCount()
	{
		return choiceSet.getCount();
	}

	public Formula getMaxSelections()
	{
		return choiceSet.getMaxSelections();
	}

	public void setCount(Formula formulaFor)
	{
		choiceSet.setCount(formulaFor);
	}

	public void setMaxSelections(Formula formulaFor)
	{
		choiceSet.setMaxSelections(formulaFor);
	}

	public int transformerHashCode()
	{
		return choiceSet.hashCode();
	}

	public boolean transformerEquals(AbstractTransformer<?> at)
	{
		return at == this || choiceSet.equals(choiceSet);
	}

	public Set<? extends PObject> getBaseSet(PlayerCharacter pc)
	{
		return choiceSet.getSet(pc);
	}

	public String getChooseType()
	{
		return choiceSet.getChooseType();
	}

	public void setChooseType(String key)
	{
		choiceSet.setChooseType(key);
	}

	public String getBaseChoiceLSTformat()
	{
		return choiceSet.getLSTformat();
	}

	public Class<? extends PObject> getBaseChoiceClass()
	{
		return choiceSet.getChoiceClass();
	}
}
