package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.cdom.inst.PCClassLevel;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

public class PCClassLevelLoader {

	public static void parseLine(LoadContext context, PCClassLevel target,
			String lstLine, CampaignSourceEntry source) throws PersistenceLayerException {
		final StringTokenizer colToken = new StringTokenizer(lstLine,
				SystemLoader.TAB_DELIM);
		//Throw away name:
		colToken.nextToken();

		while (colToken.hasMoreTokens()) {
			String colString = colToken.nextToken().trim();
			int idxColon = colString.indexOf(':');
			if (idxColon == -1) {
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ colString);
				return;
			} else if (idxColon == 0) {
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ colString);
				return;
			}
			String key = colString.substring(0, idxColon);
			String value = (idxColon == colString.length() - 1) ? null : colString.substring(idxColon + 1);

			PCClassLstToken token = TokenStore.inst().getToken(PCClassLstToken.class,
					key);

			if (token == null) {
				if (!PObjectLoader.parseTag(context, target, key, value)) {
					Logging.errorPrint("Illegal pcclass Token '" + key + "' for "
							+ target.getDisplayName() + " in " + source.getURI()
							+ " of " + source.getCampaign() + ".");
				}
			} else {
				LstUtils.deprecationCheck(token, target, value);
				if (!token.parse(target, value, target.getClassLevel())) {
					Logging.errorPrint("Error parsing token " + key + " in pcclass "
							+ target.getDisplayName() + ':' + source.getURI() + ':'
							+ value + "\"");
				}
			}
		}
	}

}
