package pcgen.rules.context;

import java.util.Collection;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.LSTWriteable;

public interface AssociatedChanges<T>
{
	public boolean includesGlobalClear();

	public Collection<LSTWriteable> getAdded();

	public Collection<LSTWriteable> getRemoved();

	public MapToList<LSTWriteable, AssociatedPrereqObject> getAddedAssociations();

	public MapToList<LSTWriteable, AssociatedPrereqObject> getRemovedAssociations();

}
