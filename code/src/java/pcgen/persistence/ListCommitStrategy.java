package pcgen.persistence;

import java.net.URI;
import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;

public interface ListCommitStrategy
{

	public AssociatedPrereqObject addToMasterList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<?>> list,
		LSTWriteable allowed);

	public Collection<CDOMReference> getMasterLists(
		Class<? extends CDOMList<?>> cl);

	public boolean hasMasterLists();

	public AssociatedChanges<LSTWriteable> getChangesInMasterList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<?>> swl);

	public <T extends CDOMObject> AssociatedPrereqObject addToList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> list, CDOMReference<T> allowed);

	public Collection<CDOMReference<CDOMList<? extends CDOMObject>>> getChangedLists(
		CDOMObject owner, Class<? extends CDOMList<?>> cl);

	public void removeAllFromList(String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<?>> swl);

	public <T extends CDOMObject> void removeFromList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl,
		CDOMReference<T> ref);

	public <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesInList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl);

	public void setSourceURI(URI sourceURI);

	public void setExtractURI(URI sourceURI);

	public void clearAllMasterLists(String tokenName, CDOMObject owner);
}
