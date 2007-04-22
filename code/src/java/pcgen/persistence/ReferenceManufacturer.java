package pcgen.persistence;

import pcgen.cdom.base.CDOMReference;
import pcgen.core.PObject;

public interface ReferenceManufacturer<T extends PObject>
{
	public CDOMReference<T> getReference(String key);

	public Class<T> getCDOMClass();
}
