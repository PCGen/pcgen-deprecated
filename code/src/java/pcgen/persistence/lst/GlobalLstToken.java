/*
 * Created on Sep 7, 2005
 */
package pcgen.persistence.lst;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;

/**
 * @author djones4
 */
public interface GlobalLstToken extends LstToken
{

	/**
	 * Parse line
	 * @param obj
	 * @param value
	 * @param anInt
	 * 
	 * @return true if OK
	 * 
	 * @throws PersistenceLayerException
	 */
	public boolean parse(PObject obj, String value, int anInt)
		throws PersistenceLayerException;

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException;
	
	public String[] unparse(LoadContext context, CDOMObject obj);
}
