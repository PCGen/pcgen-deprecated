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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.character.CharacterDataStore;

public class ChosenChoiceSet<T> implements PrimitiveChoiceSet<T>
{

	private final CDOMObject baseObject;

	private final Class<T> choiceCl;

	private final String name;

	public ChosenChoiceSet(CDOMObject parent, Class<T> choiceClass,
		String choiceName)
	{
		super();
		if (parent == null)
		{
			throw new IllegalArgumentException("Choice Parent cannot be null");
		}
		if (choiceClass == null)
		{
			throw new IllegalArgumentException("Choice Class cannot be null");
		}
		if (choiceName == null)
		{
			throw new IllegalArgumentException(
				"Choice Name String cannot be null");
		}
		baseObject = parent;
		name = choiceName;
		choiceCl = choiceClass;
	}

	public String getLSTformat()
	{
		return null;
	}

	public Class<T> getChoiceClass()
	{
		return choiceCl;
	}

	public Set<T> getSet(CharacterDataStore pc)
	{
		PCGenGraph graph = pc.getActiveGraph();
		List<PCGraphEdge> edges = graph.getOutwardEdgeList(baseObject);
		Set<T> returnSet = new HashSet<T>();
		for (PCGraphEdge edge : edges)
		{
			PrereqObject sink = edge.getNodeAt(1);
			if (sink instanceof ChoiceSet)
			{
				ChoiceSet<?> cs = (ChoiceSet<?>) sink;
				if (name.equals(cs.getName()))
				{
					AssociationListKey<T> listKey =
							(AssociationListKey<T>) edge
								.getAssociation(AssociationKey.CHOICE_KEY);
					List<T> assoc = edge.getAssociationListFor(listKey);
					returnSet.addAll(assoc);
				}
			}
		}
		return returnSet;
	}
}
