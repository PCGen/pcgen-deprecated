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

import java.util.ArrayList;
import java.util.List;

import pcgen.base.graph.core.DirectionalSetMapGraph;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.helper.CharacterDescription;

public class PCGenGraph extends
		DirectionalSetMapGraph<PrereqObject, PCGraphEdge>
{

	private final PCGraphRoot root = new PCGraphRoot();

	public PrereqObject getRoot()
	{
		return root;
	}

	public <T extends PrereqObject> List<T> getGrantedNodeList(Class<T> name)
	{
		// Long/Slow implementation...
		List<T> list = new ArrayList<T>();
		for (PrereqObject pro : getNodeList())
		{
			if (name.isInstance(pro))
			{
				list.add(name.cast(pro));
			}
		}
		return list;
	}

	public <T extends CDOMObject> T getGrantedNode(Class<T> name, String s)
	{
		// Long/Slow implementation...
		for (PrereqObject pro : getNodeList())
		{
			if (name.isInstance(pro))
			{
				T po = name.cast(pro);
				if (s.equals(po.getKeyName()))
				{
					return po;
				}
			}
		}
		return null;
	}

	public <T extends PrereqObject> int getGrantedNodeCount(Class<T> name)
	{
		// Long/Slow implementation...
		int count = 0;
		for (PrereqObject pro : getNodeList())
		{
			if (name.isInstance(pro))
			{
				count++;
			}
		}
		return count;
	}

	public CharacterDescription getDescription()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends CDOMObject> boolean containsGranted(Class<T> name,
		String templateKey)
	{
		for (PrereqObject pro : getNodeList())
		{
			if (name.isInstance(pro))
			{
				if (((CDOMObject) pro).getKeyName().equalsIgnoreCase(
					templateKey))
				{
					return true;
				}
			}
		}
		return false;
	}
}
