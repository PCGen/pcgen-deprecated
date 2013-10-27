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
import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseActionContainer;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.TokenSupport;
import pcgen.rules.persistence.token.DeferredToken;

public abstract class LoadContext
{

	public final ListContext list;

	public final ObjectContext obj;

	public final ReferenceContext ref;

	public LoadContext(ListContext lc, ObjectContext oc)
	{
		ref = new ReferenceContext();
		list = lc;
		obj = oc;
	}

	public <T extends PrereqObject> CDOMGroupRef<T> groupChildNodesOfClass(
			PrereqObject parent, Class<T> child)
	{
		/*
		 * Create a new Group in the graph and then (defer to end of build)
		 * create edges between the new Group and all of the children of the
		 * given parent.
		 */
		// TODO FIXME
		return null;
	}

	private int writeMessageCount = 0;

	public void addWriteMessage(String string)
	{
		System.err.println("!!" + string);
		// TODO FIXME Silently consume for now - these are message generated
		// during LST write...
		writeMessageCount++;
	}

	public int getWriteMessageCount()
	{
		return writeMessageCount;
	}

	/**
	 * Sets the extract URI. This is a shortcut for setting the URI on both the
	 * graph and obj members.
	 * 
	 * @param extractURI
	 */
	public void setExtractURI(URI extractURI)
	{
		getObjectContext().setExtractURI(extractURI);
		ref.setExtractURI(extractURI);
		getGraphContext().setExtractURI(extractURI);
		getListContext().setExtractURI(extractURI);
	}

	/**
	 * Sets the source URI. This is a shortcut for setting the URI on both the
	 * graph and obj members.
	 * 
	 * @param sourceURI
	 */
	public void setSourceURI(URI sourceURI)
	{
		getObjectContext().setSourceURI(sourceURI);
		ref.setSourceURI(sourceURI);
		getGraphContext().setSourceURI(sourceURI);
		getListContext().setSourceURI(sourceURI);
	}

	/*
	 * Get the type of context we're running in (either Editor or Runtime)
	 */
	public abstract String getContextType();

	public ListContext getGraphContext()
	{
		return list;
	}

	public ObjectContext getObjectContext()
	{
		return obj;
	}

	public ListContext getListContext()
	{
		return list;
	}

	public void commit()
	{
		getGraphContext().commit();
		getListContext().commit();
		getObjectContext().commit();
	}

	public void decommit()
	{
		getGraphContext().decommit();
		getListContext().decommit();
		getObjectContext().decommit();
	}

	public void resolveReferences()
	{

	}

	public void resolveDeferredTokens()
	{
		for (DeferredToken<? extends CDOMObject> token : TokenLibrary
				.getDeferredTokens())
		{
			processRes(token);
		}
		for (CDOMObject cdo : ref.getAllConstructedObjects())
		{
			String cs = cdo.get(StringKey.CHOOSE_BACKUP);
			if (cs == null)
			{
				// Nothing to do (didn't have a CHOOSE that broke)
				continue;
			}
			ChooseActionContainer container = cdo.getChooseContainer();
			if (container.getChoiceSet() != null)
			{
				// Indicates a CHOOSE token worked (except mod cases in runtime)
				continue;
			}
			// System.err.println(container.getActors());
			// System.err.println("@" + cs);
		}
	}

	private <T extends CDOMObject> void processRes(DeferredToken<T> token)
	{
		Class<T> cl = token.getObjectClass();
		for (T po : ref.getConstructedCDOMObjects(cl))
		{
			token.process(this, po);
		}
	}

	private TokenSupport support = new TokenSupport();

	public <T extends CDOMObject> PrimitiveChoiceSet<T> getChoiceSet(
			Class<T> poClass, String value)
	{
		return support.getChoiceSet(this, poClass, value);
	}

	public <T extends CDOMObject> PrimitiveChoiceFilter<T> getPrimitiveChoiceFilter(
			Class<T> cl, String key)
	{
		return support.getPrimitive(this, cl, key);
	}

	public <T> boolean processSubToken(T cdo, String tokenName,
			String key, String value) throws PersistenceLayerException
	{
		return support.processSubToken(this, cdo, tokenName, key, value);
	}

	public <T extends CDOMObject> boolean processToken(T derivative,
			String typeStr, String argument) throws PersistenceLayerException
	{
		return support.processToken(this, derivative, typeStr, argument);
	}

	public <T> String[] unparse(T cdo, String tokenName)
	{
		return support.unparse(this, cdo, tokenName);
	}

	public <T> Collection<String> unparse(T cdo)
	{
		return support.unparse(this, cdo);
	}

	public <T extends CDOMObject> PrimitiveChoiceSet<?> getChoiceSet(
			CDOMObject cdo, String key, String val)
			throws PersistenceLayerException
	{
		return support.getChoiceSet(this, cdo, key, val);
	}

	public Prerequisite getPrerequisite(String string, String value)
			throws PersistenceLayerException
	{
		return support.getPrerequisite(this, string, value);
	}

	public <T extends CDOMObject> T cloneConstructedCDOMObject(T cdo, String newName)
	{
		T newObj = obj.cloneConstructedCDOMObject(cdo, newName);
		ref.importObject(newObj);
		return newObj;
	}
}
