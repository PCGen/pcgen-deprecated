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
package plugin.pretokens.test;

import junit.framework.TestCase;

import org.junit.Before;

import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public abstract class AbstractCDOMPreTestTestCase<T extends PObject> extends
		TestCase
{

	PlayerCharacter pc;

	@Override
	@Before
	public void setUp()
	{
		pc = new PlayerCharacter(false);
	}

	public abstract Class<T> getCDOMClass();

	public abstract Class<? extends PObject> getFalseClass();

	public <TT extends PObject> TT getObject(Class<TT> cl, String s)
	{
		TT obj;
		try
		{
			obj = cl.newInstance();
			obj.setKeyName(s);
			obj.put(StringKey.NAME, s);
			return obj;
		}
		catch (InstantiationException e)
		{
			fail(e.getLocalizedMessage());
		}
		catch (IllegalAccessException e)
		{
			fail(e.getLocalizedMessage());
		}
		return null;
	}

	public PObject getFalseObject(String s)
	{
		return getObject(getFalseClass(), s);
	}

	public T getObject(String s)
	{
		return getObject(getCDOMClass(), s);
	}

	public PCGraphGrantsEdge grantObject(PrereqObject obj)
	{
		PCGenGraph graph = pc.getActiveGraph();
		PrereqObject root = graph.getRoot();
		graph.addNode(obj);
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(root, obj, "TestCase");
		graph.addEdge(edge);
		return edge;
	}

	public PObject grantCDOMObject(String s)
	{
		T object = getObject(s);
		grantObject(object);
		return object;
	}

	public PObject grantFalseObject(String s)
	{
		PObject object = getFalseObject(s);
		grantObject(object);
		return object;
	}
}
