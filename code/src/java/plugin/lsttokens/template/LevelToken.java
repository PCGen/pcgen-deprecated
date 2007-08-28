/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.template;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.LstUtils;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.persistence.lst.TokenStore;
import pcgen.util.Logging;

/**
 * Class deals with LEVEL Token
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2007-01-03 02:53:55 -0500
 * (Wed, 03 Jan 2007) $
 * 
 * @version $Revision$
 */
public class LevelToken extends AbstractToken implements PCTemplateLstToken
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "LEVEL";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.PCTemplateLstToken#parse(pcgen.core.PCTemplate,
	 *      java.lang.String)
	 */
	public boolean parse(PCTemplate template, String value)
	{
		if (".CLEAR".equals(value))
		{
			template.clearLevelAbilities();
			return true;
		}

		final StringTokenizer tok = new StringTokenizer(value, ":");
		final String levelStr = tok.nextToken();
		final int level;
		try
		{
			level = Integer.parseInt(levelStr);
		}
		catch (NumberFormatException ex)
		{
			Logging.errorPrint("Unknown Level in " + getTokenName() + ": "
				+ levelStr);
			Logging.errorPrint("  Entire Token was: " + value);
			return false;
		}
		final String typeStr = tok.nextToken();

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PCTemplateLstToken.class);
		PCTemplateLstToken token = (PCTemplateLstToken) tokenMap.get(typeStr);

		if (token != null)
		{
			template.addLevelAbility(level, typeStr, tok.nextToken());
		}
		else
		{
			String tagValue = value.substring(levelStr.length() + 1);
			try
			{
				return PObjectLoader.parseTagLevel(template, tagValue, level);
			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Failed to parse " + value + ".", e);
				return false;
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
		throws PersistenceLayerException
	{
		if (".CLEAR".equals(value))
		{
			context.getGraphContext().removeAll(getTokenName(), template);
			return true;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.COLON);

		final String levelStr = tok.nextToken();
		try
		{
			/*
			 * Note this test of integer (even if it doesn't get used outside
			 * this try) is necessary for catching errors.
			 */
			int lvl = Integer.parseInt(levelStr);
			if (lvl <= 0)
			{
				Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Level was <= 0): " + lvl);
				Logging.errorPrint("  Line was: " + value);
				return false;
			}
		}
		catch (NumberFormatException ex)
		{
			Logging.errorPrint("Misunderstood Level value: " + levelStr
				+ " in " + getTokenName());
			return false;
		}
		Prerequisite prereq = getPrerequisite("PRELEVEL:" + levelStr);
		if (prereq == null)
		{
			Logging.errorPrint("  result of an error reading level range in "
				+ getTokenName() + ": " + value);
			return false;
		}

		if (!tok.hasMoreTokens())
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": requires 3 colon separated elements (has one): " + value);
			return false;
		}
		final String typeStr = tok.nextToken();
		if (!tok.hasMoreTokens())
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": requires 3 colon separated elements (has two): " + value);
			return false;
		}
		String argument = tok.nextToken();
		String standardizedPrereq =
				getPrerequisiteString(context, Collections
					.singletonList(prereq));
		PCTemplate derivative = template.getPseudoTemplate(standardizedPrereq);
		derivative.put(ObjectKey.PSEUDO_PARENT, template);
		derivative.addPrerequisite(prereq);
		context.getGraphContext().grant(getTokenName(), template, derivative);
		PCTemplateLstToken token =
				TokenStore.inst().getToken(PCTemplateLstToken.class, typeStr);
		if (token == null)
		{
			if (!PObjectLoader.parseTag(context, derivative, typeStr, argument))
			{
				Logging.errorPrint("Illegal template Token '" + typeStr + "' '"
					+ value + "' for " + template.getDisplayName());
				return false;
			}
		}
		else
		{
			LstUtils.deprecationCheck(token, derivative, argument);
			if (!token.parse(context, derivative, argument))
			{
				Logging.errorPrint("Error Parsing Token '" + typeStr
					+ " in template " + template.getDisplayName());
				return false;
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		AssociatedChanges<PCTemplate> changes =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					pct, PCTemplate.class);
		if (changes == null)
		{
			return null;
		}

		Set<String> set = new TreeSet<String>();

		for (LSTWriteable lstw : changes.getAdded())
		{
			PCTemplate pctChild = PCTemplate.class.cast(lstw);
			if (pctChild.getPrerequisiteCount() != 1)
			{
				context.addWriteMessage("Only one Prerequisiste allowed on "
					+ getTokenName() + " child PCTemplate");
				return null;
			}
			Prerequisite prereq = pctChild.getPrerequisiteList().get(0);
			String kind = prereq.getKind();
			if (!kind.equalsIgnoreCase("LEVEL"))
			{
				context.addWriteMessage("Prerequisiste on " + getTokenName()
					+ " derived edge must be LEVEL");
				return null;
			}
			if (!PrerequisiteOperator.GTEQ.equals(prereq.getOperator()))
			{
				context.addWriteMessage("Invalid Operator built on "
					+ getTokenName() + " derived edge");
				return null;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(prereq.getOperand()).append(':');

			for (LstToken token : TokenStore.inst().getTokenMap(
				PCTemplateLstToken.class).values())
			{
				StringBuilder sb2 = new StringBuilder();
				sb2.append(sb).append(token.getTokenName()).append(':');
				String[] s =
						((PCTemplateLstToken) token).unparse(context, pctChild);
				if (s != null)
				{
					for (String aString : s)
					{
						set.add(sb2.toString() + aString);
					}
				}
			}
			for (LstToken token : TokenStore.inst().getTokenMap(
				GlobalLstToken.class).values())
			{
				StringBuilder sb2 = new StringBuilder();
				sb2.append(sb).append(token.getTokenName()).append(':');
				String[] s =
						((GlobalLstToken) token).unparse(context, pctChild);
				if (s != null)
				{
					for (String aString : s)
					{
						set.add(sb2.toString() + aString);
					}
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}
}
