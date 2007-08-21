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
import java.util.List;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;

public class RuntimeObjectContext implements ObjectContext
{

	// private final String IMPORT_COMMAND = "Command executed during LST
	// Import";

	// DoubleKeyMapToList<CDOMObject, Object, CDOMCommand> commandMap =
	// new DoubleKeyMapToList<CDOMObject, Object, CDOMCommand>();

	private URI sourceURI;

	private URI extractURI;

	public void put(CDOMObject cdo, StringKey sk, String s)
	{
		if (s == null)
		{
			cdo.remove(sk);
		}
		else if (s.startsWith(Constants.LST_DOT_CLEAR))
		{
			throw new IllegalArgumentException("Cannot set a value to " + s);
		}
		else
		{
			cdo.put(sk, s);
		}
		// SetStringKeyCommand command =
		// new SetStringKeyCommand(IMPORT_COMMAND, cdo, sk, s);
		// command.setSourceURI(sourceURI);
		// commandMap.addToListFor(cdo, sk, command);
	}

	public <T> void put(CDOMObject cdo, ObjectKey<T> sk, T s)
	{
		cdo.put(sk, s);
		// SetObjectKeyCommand<T> command =
		// new SetObjectKeyCommand<T>(IMPORT_COMMAND, cdo, sk, s);
		// command.setSourceURI(sourceURI);
		// commandMap.addToListFor(cdo, sk, command);
	}

	public void put(CDOMObject cdo, IntegerKey ik, Integer i)
	{
		cdo.put(ik, i);
		// SetIntegerKeyCommand command =
		// new SetIntegerKeyCommand(IMPORT_COMMAND, cdo, ik, i);
		// command.setSourceURI(sourceURI);
		// commandMap.addToListFor(cdo, ik, command);
	}

	public void put(CDOMObject cdo, FormulaKey fk, Formula f)
	{
		cdo.put(fk, f);
	}

	public void put(CDOMObject obj, VariableKey vk, Formula f)
	{
		obj.put(vk, f);
	}

	public boolean containsListFor(CDOMObject cdo, ListKey<?> key)
	{
		return cdo.containsListFor(key);
	}

	public <T> void addToList(CDOMObject cdo, ListKey<T> key, T value)
	{
		cdo.addToListFor(key, value);
		// AddToListKeyCommand<T> command =
		// new AddToListKeyCommand<T>(IMPORT_COMMAND, cdo, key, value);
		// command.setSourceURI(sourceURI);
		// commandMap.addToListFor(cdo, key, command);
	}

	public void removeList(CDOMObject cdo, ListKey<?> lk)
	{
		cdo.removeListFor(lk);
	}

	public <T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val)
	{
		cdo.removeFromListFor(lk, val);
	}

	public String getString(CDOMObject cdo, StringKey sk)
	{
		return cdo.get(sk);
	}

	public Integer getInteger(CDOMObject cdo, IntegerKey ik)
	{
		return cdo.get(ik);
	}

	public Formula getFormula(CDOMObject cdo, FormulaKey fk)
	{
		return cdo.get(fk);
	}

	public Formula getVariable(CDOMObject obj, VariableKey key)
	{
		return obj.get(key);
	}

	public Set<VariableKey> getVariableKeys(CDOMObject obj)
	{
		return obj.getVariableKeys();
	}

	public <T> T getObject(CDOMObject cdo, ObjectKey<T> ik)
	{
		return cdo.get(ik);
		// List<CDOMCommand> list = cdo.getListFor(MapKey.COMMANDS, ik);
		// if (list == null || list.isEmpty())
		// {
		// return null;
		// }
		// for (int i = list.size() - 1; i >= 0; i--)
		// {
		// CDOMCommand c = list.get(i);
		// if (extractURI == null)
		// {
		// throw new IllegalStateException(
		// "Need to define what to do here");
		// }
		// else if (extractURI.equals(c.getSourceURI()))
		// {
		// return ((SetObjectKeyCommand<T>) c).getObjectValue();
		// }
		// }
		// return null;
	}

	public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
	{
		return new CollectionChanges<T>(cdo.getListFor(lk), null, false);
		// List<CDOMCommand> list = cdo.getListFor(MapKey.COMMANDS, lk);
		// if (list == null || list.isEmpty())
		// {
		// return null;
		// }
		// Changes<T> changes = new Changes<T>();
		// for (int i = list.size() - 1; i >= 0; i--)
		// {
		// CDOMCommand c = list.get(i);
		// if (extractURI == null)
		// {
		// throw new IllegalStateException(
		// "Need to define what to do here");
		// }
		// else if (extractURI.equals(c.getSourceURI()))
		// {
		// if (c instanceof AddToListKeyCommand)
		// {
		// T added = ((AddToListKeyCommand<T>) c).getListValue();
		// changes.addAdded(added);
		// }
		// // if (c instanceof RemoveToListKeyCommand)
		// // {
		// // T removed = ((RemoveToListKeyCommand<T>) c).getListValue();
		// // changes.addRemoved(removed);
		// // }
		// if (c instanceof ClearListKeyCommand)
		// {
		// changes.setGloballyCleared();
		// }
		// }
		// }
		// return changes.isEmpty() ? null : changes;
	}

	public URI getExtractURI()
	{
		return extractURI;
	}

	public void setExtractURI(URI extractURI)
	{
		this.extractURI = extractURI;
	}

	public URI getSourceURI()
	{
		return sourceURI;
	}

	public void setSourceURI(URI sourceURI)
	{
		this.sourceURI = sourceURI;
	}
}
