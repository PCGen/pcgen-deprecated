/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.restriction;

import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.Restriction;
import pcgen.core.CompanionList;
import pcgen.core.character.Follower;

public class FollowerRestriction implements Restriction<Follower>
{

	private final CDOMSingleRef<CompanionList> compList;

	private final Class<Follower> targetClass = Follower.class;

	public FollowerRestriction(CDOMSingleRef<CompanionList> cl)
	{
		compList = cl;
	}

	public boolean qualifies(Follower pro)
	{
		if (!pro.getClass().equals(targetClass))
		{
			// CONSIDER This is an Error, or is false sufficient?
			return false;
		}
		CompanionList cl = compList.resolvesTo();
		/*
		 * TODO FIXME This probably needs to receive a reference to the active PC?
		 * That is actually rather ugly, but since CompanionList is now a "list" 
		 * in the Graph, that is probably what needs to happen...
		 */
		return false; //compList.containsRace(pro.getRaceObject());
	}

	public Class<Follower> getRestrictedClass()
	{
		return targetClass;
	}

	public Class<Follower> getRestrictedType()
	{
		return Follower.class;
	}

	public String toLSTform()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
