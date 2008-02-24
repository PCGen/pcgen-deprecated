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
package pcgen.rules.context;

import java.net.URI;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;

public interface GraphCommitStrategy
{

	public void setSourceURI(URI source);

	public void setExtractURI(URI uri);

	public void setLine(int line);
	
	public AssociatedPrereqObject grant(String sourceToken, CDOMObject obj,
		PrereqObject pro);

	public void remove(String tokenName, CDOMObject obj, PrereqObject child);

	public void removeAll(String tokenName, CDOMObject obj);

	public <T extends PrereqObject & LSTWriteable> AssociatedChanges<T> getChangesFromToken(
		String tokenName, CDOMObject pct, Class<T> name);
}
