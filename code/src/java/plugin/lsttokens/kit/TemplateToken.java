/*
 * TemplateToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 3, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit;

import java.net.URI;
import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.cdom.kit.CDOMKitTemplate;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Kit;
import pcgen.core.kit.KitTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.BaseKitLoader;
import pcgen.persistence.lst.KitLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * This class parses a TEMPLATE line from a Kit file. It handles the TEMPLATE
 * tag as well as all common tags.
 * <p>
 * <strong>Tag Name:</strong> TEMPLATE:x|x <br>
 * <strong>Variables Used (x):</strong> Text (Name of template)<br>
 * <strong>What it does:</strong><br>
 * &nbsp;&nbsp;This is a | (pipe) delimited list of templates that are granted
 * by the feat.<br>
 * <strong>Example:</strong><br>
 * &nbsp;&nbsp;<code>TEMPLATE:Celestial</code><br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Adds the "Celestial" template to the character.<br>
 * </p>
 */
public class TemplateToken extends KitLstToken implements
		CDOMSecondaryToken<CDOMKitTemplate>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "TEMPLATE";
	}

	/**
	 * Parse the TEMPLATE line. Handles the TEMPLATE tag as well as all common
	 * tags.
	 * 
	 * @param aKit
	 *            the Kit object to add this information to
	 * @param value
	 *            the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 */
	@Override
	public boolean parse(Kit aKit, String value, URI source)
			throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(value,
				SystemLoader.TAB_DELIM);
		KitTemplate kTemplate = new KitTemplate(colToken.nextToken());

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("TEMPLATE:"))
			{
				Logging.errorPrint("Ignoring second TEMPLATE tag \""
						+ colString + "\" in TemplateToken.parse");
			}
			else
			{
				if (BaseKitLoader.parseCommonTags(kTemplate, colString, source) == false)
				{
					throw new PersistenceLayerException(
							"Unknown KitTemplate info " + " \"" + colString
									+ "\"");
				}
			}
		}
		aKit.addObject(kTemplate);
		return true;
	}

	public Class<CDOMKitTemplate> getTokenClass()
	{
		return CDOMKitTemplate.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitTemplate kitTemplate,
			String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			int openLoc = tokText.indexOf('[');
			String name;
			if (openLoc == -1)
			{
				name = tokText;
			}
			else
			{
				name = tokText.substring(0, openLoc);
				String rest = tokText.substring(openLoc + 1);
				StringTokenizer subTok = new StringTokenizer(rest, "[]");
				while (subTok.hasMoreTokens())
				{
					String subStr = subTok.nextToken();
					if (subStr.startsWith("TEMPLATE:"))
					{
						String ownedTemplateName = subStr.substring(9);

						CDOMSingleRef<CDOMTemplate> ref = context.ref
								.getCDOMReference(CDOMTemplate.class,
										ownedTemplateName);
						kitTemplate.addSubTemplate(ref);
					}
					else
					{
						Logging.errorPrint("Did not understand "
								+ getTokenName() + " option: " + subStr
								+ " in line: " + value);
						return false;
					}
				}
			}
			CDOMSingleRef<CDOMTemplate> ref = context.ref.getCDOMReference(
					CDOMTemplate.class, name);
			kitTemplate.setTemplate(ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitTemplate kitTemplate)
	{
		CDOMReference<CDOMTemplate> ref = kitTemplate.getTemplate();
		Collection<CDOMReference<CDOMTemplate>> sub = kitTemplate
				.getSubTemplates();
		StringBuilder sb = new StringBuilder();
		sb.append(ref.getLSTformat());
		if (sub != null && !sub.isEmpty())
		{
			for (CDOMReference<CDOMTemplate> subTemplate : sub)
			{
				sb.append("[TEMPLATE:");
				sb.append(subTemplate.getLSTformat());
				sb.append(']');
			}
		}
		return new String[] { sb.toString() };
	}
}
