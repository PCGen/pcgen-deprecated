package pcgen.persistence;

import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;

public interface Changes<T>
{
	public boolean hasAddedItems();

	public boolean hasRemovedItems();

	public boolean includesGlobalClear();

	public Collection<T> getAdded();

	public AssociatedPrereqObject getAddedAssociation(T added);

	public Collection<T> getRemoved();

	public AssociatedPrereqObject getRemovedAssociation(T removed);

	public boolean isEmpty();

}
