package pcgen.persistence;

import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;

public interface ListContext
{

	public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> list, LSTWriteable allowed);

	public Collection<CDOMReference> getMasterLists(
		Class<? extends CDOMList<?>> cl);

	public <T extends CDOMObject> MasterListChanges<T> getChangesInMasterList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl);

	public <T extends CDOMObject> void clearMasterList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> list);

	public <T extends CDOMObject> AssociatedPrereqObject addToList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> list, CDOMReference<T> allowed);

	public Collection<CDOMReference<CDOMList<? extends CDOMObject>>> getChangedLists(
		CDOMObject owner, Class<? extends CDOMList<?>> cl);

	// TODO May not need the Class<T> reference here, just make this
	// removeAllFromList?
	public <T extends CDOMObject> void removeFromList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl, Class<T> cl);

	public <T extends CDOMObject> void removeFromList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl,
		CDOMReference<T> ref);

	public <T extends CDOMObject> ListGraphChanges<T> getChangesInList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl);
}
