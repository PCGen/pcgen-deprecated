package plugin.lsttokens.kit;

import java.net.URI;

import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.KitLstToken;
import pcgen.util.Logging;

public class RollStatsToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "ROLLSTATS";
	}

	/**
	 * 
	 * @param aKit
	 *            the Kit object to add this information to
	 * @param value
	 *            the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 * @todo Implement this pcgen.persistence.lst.KitLstToken method
	 */
	@Override
	public boolean parse(Kit aKit, String value, URI source)
		throws PersistenceLayerException
	{
		Logging.errorPrint("ROLLSTATS tag not implemented yet!");
		return false;
	}
}
