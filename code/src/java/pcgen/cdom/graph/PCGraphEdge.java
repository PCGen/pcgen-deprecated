/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.graph;

import java.util.Collection;
import java.util.List;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;

public interface PCGraphEdge extends DirectionalEdge<PrereqObject>,
		AssociatedPrereqObject
{

	String getSourceToken();

	public <T> void setAssociation(AssociationKey<T> name, T cost);

	public <T> T getAssociation(AssociationKey<T> name);

	public Collection<AssociationKey<?>> getAssociationKeys();

	public boolean hasAssociations();

	public PCGraphEdge createReplacementEdge(PrereqObject gn1, PrereqObject gn2);

	public <T> void addToAssociationList(AssociationListKey<T> name, T cost);

	public <T> List<T> getAssociationListFor(AssociationListKey<T> listKey);

	public Collection<AssociationListKey<?>> getAssociationListKeys();
}
