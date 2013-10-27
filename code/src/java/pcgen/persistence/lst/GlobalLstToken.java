/*
 * Created on Sep 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package pcgen.persistence.lst;

import pcgen.core.PObject;
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
}
