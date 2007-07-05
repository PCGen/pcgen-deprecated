/*
 * KitLoader.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on September 23, 2002, 1:39 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * 
 * ???
 * 
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class KitLoader extends LstLeveledObjectFileLoader<Kit> {
	@Override
	protected void addGlobalObject(PObject pObj) {
		Kit k = (Kit) pObj;
		Globals.getKitInfo().put(k.getKeyName(), k);
	}

	@Override
	protected Kit getObjectKeyed(String aKey) {
		return Globals.getKitKeyed(aKey);
	}

	@Override
	public Kit parseLine(Kit target, String inputLine, CampaignSourceEntry source)
			throws PersistenceLayerException {

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				KitLstToken.class);

		// We will find the first ":" for the "controlling" line token
		final int idxColon = inputLine.indexOf(':');
		String key = "";
		try {
			key = inputLine.substring(0, idxColon);
		} catch (StringIndexOutOfBoundsException e) {
			// TODO Handle Exception
		}
		KitLstToken token = (KitLstToken) tokenMap.get(key);

		if (inputLine.startsWith("STARTPACK:")) {
			target = new Kit();
			target.setSourceCampaign(source.getCampaign());
			target.setSourceURI(source.getURI());
			if (kitPrereq != null) {
				target.addPreReq(KitLoader.kitPrereq);
			}
			if (globalTokens != null) {
				for (String tag : globalTokens) {
					PObjectLoader.parseTag(target, tag);
				}
			}
		}

		if (token != null) {
			final String value = inputLine.substring(idxColon + 1);
			LstUtils.deprecationCheck(token, target, value);
			if (!token.parse(target, value, source.getURI())) {
				Logging.errorPrint("Error parsing Kit tag "
						+ target.getDisplayName() + ':' + source.getURI()
						+ ':' + inputLine + "\"");
			}
		} else {
			Logging.errorPrint("Unknown kit info " + source.toString() + ":"
					+ " \"" + inputLine + "\"");
		}

		return target;
	}

	@Override
	protected void performForget(Kit objToForget) {
		// FIXME Auto-generated method stub

	}

	static List<String> globalTokens = null;

	static Prerequisite kitPrereq = null;

	public static void addGlobalToken(String string) {
		if (globalTokens == null) {
			globalTokens = new ArrayList<String>();
		}
		globalTokens.add(string);
	}

	public static void setKitPrerequisite(Prerequisite p) {
		kitPrereq = p;
	}

	public static void clearGlobalTokens() {
		globalTokens = null;
	}

	public static void clearKitPrerequisites() {
		kitPrereq = null;
	}
	
	@Override
	protected void loadLstFile(LoadContext context, CampaignSourceEntry cse) {
		clearGlobalTokens();
		clearKitPrerequisites();
		super.loadLstFile(context, cse);
	}

	@Override
	public Class<Kit> getLoadClass() {
		return Kit.class;
	}

	@Override
	protected Kit parseLine(LoadContext context, Kit kit, String line,
			CampaignSourceEntry source) {
		int tabLoc = line.indexOf("\t");
		String firstToken;
		if (tabLoc == -1) {
			// Error??
			firstToken = line;
		} else {
			firstToken = line.substring(0, tabLoc);
		}
		String restOfLine = line.substring(tabLoc + 1);

		int colonLoc = firstToken.indexOf(":");
		if (colonLoc == -1) {
			Logging.errorPrint("Invalid Kit Line: " + line);
			return kit;
		}
		String key = firstToken.substring(0, colonLoc);
		/*
		 * TODO FIXME This should really be in the STARTPACK Token, but it's
		 * here temporarily while the KitTokens do not take in the source - that
		 * should eventually be remedied??
		 */
		if ("STARTPACK".equals(key)) {
			String name = firstToken.substring(colonLoc + 1);
			Kit thisTarget = context.ref.silentlyGetConstructedCDOMObject(getLoadClass(), name);
			if (thisTarget != kit || kit == null) {
				kit = context.ref.constructCDOMObject(getLoadClass(), name);
				// No need to set the name - done in STARTPACK
				// FIXME Well, need to do it until the tokens are actually called :)
				kit.setName(name);
				kit.setSourceCampaign(source.getCampaign());
				kit.setSourceURI(source.getURI());
			}
		}
		KitLstToken token = TokenStore.inst().getToken(KitLstToken.class, key);

		if (token == null) {
			Logging.errorPrint("Illegal Kit Token '" + key + "' for "
					+ kit.getDisplayName() + " in " + source.getURI() + " of "
					+ source.getCampaign() + ".");
		} else {
			LstUtils.deprecationCheck(token, kit, restOfLine);
//			FIXME TODO Commented out for to avoid attempt at duplicate AbilityInfo load
//			if (!token.parse(kit, line)) {
//				Logging.errorPrint("Error parsing token " + key + " in Kit "
//						+ kit.getDisplayName() + ':' + source.getFile() + ':'
//						+ restOfLine + "\"");
//			}
		}
		return kit;
	}
}
