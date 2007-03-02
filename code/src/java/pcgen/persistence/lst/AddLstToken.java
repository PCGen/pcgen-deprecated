package pcgen.persistence.lst;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;

/**
 * @author thpr
 */
public interface AddLstToken extends LstToken {
	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException;
}
