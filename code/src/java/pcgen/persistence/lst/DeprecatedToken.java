/*
 * Created on Sep 2, 2005
 */

package pcgen.persistence.lst;

import pcgen.cdom.base.CDOMObject;

/**
 * @author djones4
 */

public interface DeprecatedToken
{

	/**
	 * Get the message to output.
	 * 
	 * @param obj The object being built when the deprecated token was encountered.
	 * @param value The value of the deprecated token.
	 * 
	 * @return A message to display to the user about why the token was deprecated
	 * and how they can fix it.  This message should be i18n.
	 */
	public String getMessage(CDOMObject obj, String value);
}
