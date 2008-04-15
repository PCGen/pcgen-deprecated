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

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.inst.SimpleAssociatedObject;
import pcgen.cdom.reference.CDOMSimpleSingleRef;
import pcgen.character.CharacterDataStore;
import plugin.pretokens.testsupport.SimpleRulesDataStore;

public abstract class AbstractCDOMPreTestTestCase<T extends CDOMObject> extends
		TestCase
{

	CharacterDataStore pc;
	SimpleRulesDataStore rules;

	@Override
	@Before
	public void setUp()
	{
		rules = new SimpleRulesDataStore();
		pc = new CharacterDataStore(rules);
	}

	public abstract Class<T> getCDOMClass();

	public abstract Class<? extends CDOMObject> getFalseClass();

	public <TT extends CDOMObject> TT getObject(Class<TT> cl, String s)
	{
		TT obj;
		try
		{
			obj = cl.newInstance();
			obj.put(StringKey.KEY_NAME, s);
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

	public CDOMObject getFalseObject(String s)
	{
		return getObject(getFalseClass(), s);
	}

	public T getObject(String s)
	{
		return getObject(getCDOMClass(), s);
	}

	public PCGraphGrantsEdge grantObject(PrereqObject obj)
	{
		return grantObject(pc.getActiveGraph().getRoot(), obj);
	}

	protected PCGraphGrantsEdge grantObject(PrereqObject root, PrereqObject obj)
	{
		PCGenGraph graph = pc.getActiveGraph();
		graph.addNode(root);
		graph.addNode(obj);
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(root, obj, "TestCase");
		graph.addEdge(edge);
		return edge;
	}

	public CDOMObject grantCDOMObject(String s)
	{
		T object = getObject(s);
		grantObject(object);
		return object;
	}

	public CDOMObject grantFalseObject(String s)
	{
		CDOMObject object = getFalseObject(s);
		grantObject(object);
		return object;
	}

	public <TT extends CDOMObject> AssociatedPrereqObject addToList(
			String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<TT>> list,
			CDOMReference<TT> allowed)
	{
		SimpleAssociatedObject a = new SimpleAssociatedObject();
		a.setAssociation(AssociationKey.TOKEN, tokenName);
		owner.putToList(list, allowed, a);
		return a;
	}

	public <TT extends CDOMObject> CDOMReference<TT> getReference(Class<TT> cl,
			String name)
	{
		return new CDOMSimpleSingleRef<TT>(cl, name);
	}

}
