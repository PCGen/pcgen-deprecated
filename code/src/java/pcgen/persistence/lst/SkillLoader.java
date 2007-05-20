/*
 * SkillLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class SkillLoader extends LstObjectFileLoader<Skill>
{
	/** Creates a new instance of SkillLoader */
	public SkillLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public void parseLine(Skill aSkill, String lstLine,
		CampaignSourceEntry source) throws PersistenceLayerException
	{
		final StringTokenizer colToken =
			new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(SkillLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = Constants.EMPTY_STRING;
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				// TODO Handle Exception
			}
			SkillLstToken token = (SkillLstToken) tokenMap.get(key);

			if ("REQ".equals(colString))
			{
				Logging.errorPrint("You are using a deprecated tag "
						+ "(REQ) in Skills " + aSkill.getDisplayName() + ':'
						+ source.getURI() + ':' + colString);
				Logging.errorPrint("  Use USEUNTRAINED instead");
				aSkill.setRequired(true);
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, aSkill, value);
				if (!token.parse(aSkill, value))
				{
					Logging.errorPrint("Error parsing skill "
						+ aSkill.getDisplayName() + ':' + source.getURI() + ':'
						+ colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(aSkill, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal skill info '" + lstLine + "' in "
					+ source.toString());
			}
		}

		completeObject(source, aSkill);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	@Override
	protected Skill getObjectKeyed(String aKey)
	{
		return Globals.getSkillKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(final Skill objToForget)
	{
		Globals.getSkillList().remove(objToForget);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		// TODO - Create Globals.addSkill(pObj);
		Globals.getSkillList().add((Skill) pObj);
	}

	@Override
	public void parseToken(LoadContext context, Skill skill, String key, String value, CampaignSourceEntry source) throws PersistenceLayerException {
		SkillLstToken token = TokenStore.inst().getToken(SkillLstToken.class,
				key);

		if (token == null) {
			if (!PObjectLoader.parseTag(context, skill, key, value)) {
				Logging.errorPrint("Illegal skill Token '" + key + "' for "
						+ skill.getDisplayName() + " in " + source.getURI()
						+ " of " + source.getCampaign() + ".");
			}
		} else {
			LstUtils.deprecationCheck(token, skill, value);
			if (!token.parse(context, skill, value))
			{
				Logging.errorPrint("Error parsing token " + key + " in skill "
						+ skill.getDisplayName() + ':' + source.getURI() + ':'
						+ value + "\"");
			}
		}
	}
	
	@Override
	public Class<Skill> getLoadClass() {
		return Skill.class;
	}
}
