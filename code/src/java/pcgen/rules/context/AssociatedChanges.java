package pcgen.rules.context;

import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.util.MapToList;

public interface AssociatedChanges<T>
{
	public boolean hasRemovedItems();

	public boolean includesGlobalClear();

	public boolean hasAddedItems();

	public Collection<LSTWriteable> getAdded();

	public Collection<LSTWriteable> getRemoved();

	public MapToList<LSTWriteable, AssociatedPrereqObject> getAddedAssociations();

	public MapToList<LSTWriteable, AssociatedPrereqObject> getRemovedAssociations();

}
