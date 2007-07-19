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
package pcgen.persistence;

import java.net.URI;
import java.util.Set;

import pcgen.cdom.base.CDOMEdgeReference;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.core.PObject;

public interface GraphContext
{

	public URI setSourceURI(URI source);

	public URI setExtractURI(URI uri);

	/*
	 * TODO This is only used for Aggregator unparse in RepeatLevel - change Agg
	 * cleanup method?
	 */
	public Set<PCGraphEdge> getChildLinksFromToken(String tokenName,
		CDOMObject obj);

	public PCGraphGrantsEdge grant(String sourceToken, PrereqObject obj,
		PrereqObject pro);

	public void remove(String tokenName, CDOMObject obj, PrereqObject child);

	public void removeAll(String tokenName, PrereqObject obj);

	public <T extends PrereqObject & LSTWriteable> GraphChanges<T> getChangesFromToken(
		String tokenName, CDOMObject pct, Class<T> name);

	public <T extends PrereqObject, A> CDOMEdgeReference getEdgeReference(
		PObject parent, Class<T> childClass, String childName,
		Class<A> assocClass);

	public EquipmentHead getEquipmentHead(Equipment eq, int i);

	public EquipmentHead getEquipmentHeadReference(Equipment eq, int i);
}
