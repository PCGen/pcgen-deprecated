package pcgen.persistence;

import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.LSTWriteable;

public interface GraphChanges<T>
{
	public boolean hasRemovedItems();

	public boolean includesGlobalClear();

	public boolean hasAddedItems();

	public Collection<LSTWriteable> getAdded();

	public Collection<LSTWriteable> getRemoved();

	public AssociatedPrereqObject getAddedAssociation(LSTWriteable added);

	public AssociatedPrereqObject getRemovedAssociation(LSTWriteable added);

}
