package pcgen.persistence;

import java.util.Collection;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.LSTWriteable;

public interface GraphChanges<T>
{
	public boolean hasRemovedItems();

	public boolean includesGlobalClear();

	public boolean hasAddedItems();

	public Collection<LSTWriteable> getAdded();

	public Collection<LSTWriteable> getRemoved();

	public MapToList<LSTWriteable, AssociatedPrereqObject> getAddedAssociations();

	public MapToList<LSTWriteable, AssociatedPrereqObject> getRemovedAssociations();

}
