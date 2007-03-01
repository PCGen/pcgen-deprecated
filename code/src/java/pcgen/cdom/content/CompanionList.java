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
package pcgen.cdom.content;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.Race;

public class CompanionList extends ConcretePrereqObject
{

	private final Set<CDOMReference<Race>> companionList;

	private final String companionType;

	private int followerAdj = 0;

	public CompanionList(String type, Collection<CDOMReference<Race>> raceList)
	{
		if (type == null)
		{
			throw new IllegalArgumentException(
				"CompanionList requires a non-null type");
		}
		if (raceList == null)
		{
			throw new IllegalArgumentException(
				"CompanionList requires a non-null list");
		}
		if (raceList.size() == 0)
		{
			throw new IllegalArgumentException(
				"CompanionList requires a non-empty list");
		}
		companionType = type;
		companionList = new HashSet<CDOMReference<Race>>();
		for (CDOMReference<Race> cr : raceList)
		{
			if (cr == null)
			{
				throw new IllegalArgumentException(
					"CompanionList prohibites null entries in list");
			}
			companionList.add(cr);
		}
	}

	public void setAdjustment(int followerAdjustment)
	{
		followerAdj = followerAdjustment;
	}

	public String getFollowerType()
	{
		return companionType;
	}

	public boolean containsRace(Race r)
	{
		for (CDOMReference<Race> ref : companionList)
		{
			if (ref.contains(r))
			{
				return true;
			}
		}
		return false;
	}

	public int getAdjustment()
	{
		return followerAdj;
	}

	public Set<CDOMReference<Race>> getCompanionSet()
	{
		return new HashSet<CDOMReference<Race>>(companionList);
	}
}
