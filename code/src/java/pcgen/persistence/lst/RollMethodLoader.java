package pcgen.persistence.lst;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Loads the roll methods
 */
public class RollMethodLoader
{

	/** rollMethod */
	public static final String ROLLMETHOD = "rollMethod";
	/** method */
	public static final String METHOD = "method";

	/** Constructor */
	public RollMethodLoader()
	{
		// Do Nothing
	}

	/**
	 * Parse the roll methods for the game mode
	 * 
	 * @param gameMode
	 * @param lstLine
	 * @throws PersistenceLayerException
	 */
	public void parseLine(GameMode gameMode, String lstLine, URI source)
		throws PersistenceLayerException
	{
		Map<String, String> method = new HashMap<String, String>();
		method.put(ROLLMETHOD, "");
		method.put(METHOD, "");

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(RollMethodLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// TODO Handle Exception
			}

			RollMethodLstToken token = (RollMethodLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "Roll Method", source, value);
				if (!token.parse(method, value))
				{
					Logging.errorPrint("Error parsing Roll Method:"
						+ "miscinfo.lst from the " + gameMode.getName()
						+ " Game Mode" + ':' + colString + "\"");
				}
			}
			else
			{
				Logging.errorPrint("Invalid sub tag " + token
					+ " on ROLLMETHOD line");
				throw new PersistenceLayerException("Invalid sub tag " + token
					+ " on ROLLMETHOD line");
			}

		}

		if (method.get(ROLLMETHOD).equals("") || method.get(METHOD).equals(""))
		{
			throw new PersistenceLayerException(
				"Missing required information on ROLLMETHOD line");
		}
		//Now set the penalty object in this gameMode
		gameMode.addRollingMethod(method.get(ROLLMETHOD), method.get(METHOD));
	}
}
