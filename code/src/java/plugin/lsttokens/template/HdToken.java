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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.PCTemplate;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.persistence.lst.TokenStore;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HD Token
 */
public class HdToken extends AbstractToken implements PCTemplateLstToken, CDOMPrimaryToken<CDOMTemplate>
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "HD";
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
			template.clearHitDiceStrings();
			return true;
		}

		StringTokenizer tok = new StringTokenizer(value, ":");
		String hdStr = tok.nextToken();
		String typeStr = tok.nextToken();
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PCTemplateLstToken.class);
		PCTemplateLstToken token = (PCTemplateLstToken) tokenMap.get(typeStr);

		if (token != null)
		{
			template.addHitDiceString(value);
		}
		else
		{
			String tagValue =
					value.substring(hdStr.length() + 1) + "|PREHD:" + hdStr; 
			try
			{
				return PObjectLoader.parseTag(template, tagValue);
			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Failed to parse " + value + ".", e);
				return false;
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMTemplate template, String value)
		throws PersistenceLayerException
	{
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.getGraphContext().removeAll(getTokenName(), template);
			return true;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.COLON);

		Prerequisite prereq = getPrerequisite("PREHD:" + tok.nextToken());
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
		String typeStr = tok.nextToken();
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
		CDOMTemplate derivative = template.getPseudoTemplate(standardizedPrereq);
		//derivative.put(ObjectKey.PSEUDO_PARENT, template);
		derivative.addPrerequisite(prereq);
		context.getObjectContext().give(getTokenName(), template, derivative);
		return context.processToken(derivative, typeStr, argument);
	}

	public String[] unparse(LoadContext context, CDOMTemplate pct)
	{
		Changes<CDOMTemplate> changes =
				context.getObjectContext().getGivenChanges(getTokenName(),
					pct, CDOMTemplate.class);
		Collection<CDOMTemplate> added = changes.getAdded();
		if (added == null || added.isEmpty()){
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (LSTWriteable lstw : added)
		{
			CDOMTemplate pctChild = CDOMTemplate.class.cast(lstw);
			if (pctChild.getPrerequisiteCount() != 1)
			{
				context.addWriteMessage("Only one Prerequisiste allowed on "
					+ getTokenName() + " child PCTemplate");
				return null;
			}
			StringBuilder sb = new StringBuilder();
			Prerequisite prereq = pctChild.getPrerequisiteList().get(0);
			String kind = prereq.getKind();
			if (kind == null)
			{
				if (!prereq.getOperand().equals("2"))
				{
					context.addWriteMessage("Misunderstood Prerequisiste "
						+ "built on " + getTokenName() + " derived edge");
					return null;
				}
				List<Prerequisite> subreqs = prereq.getPrerequisites();
				if (subreqs == null)
				{
					context.addWriteMessage("MULT Prerequisiste "
						+ "without subreqs built on " + getTokenName()
						+ " derived edge");
					return null;
				}
				if (subreqs.size() != 2)
				{
					context.addWriteMessage("MULT Prerequisiste "
						+ "without proper subreq count built on "
						+ getTokenName() + " derived edge");
					return null;
				}
				String lowerRange = null;
				String upperRange = null;
				for (Prerequisite sp : subreqs)
				{
					if (PrerequisiteOperator.GTEQ.equals(sp.getOperator()))
					{
						lowerRange = sp.getOperand();
					}
					else if (PrerequisiteOperator.LTEQ.equals(sp.getOperator()))
					{
						upperRange = sp.getOperand();
					}
				}
				if (lowerRange == null || upperRange == null)
				{
					context.addWriteMessage("MULT Prerequisiste "
						+ "without proper range built on " + getTokenName()
						+ " derived edge");
					return null;
				}
				try
				{
					Integer.parseInt(lowerRange);
				}
				catch (NumberFormatException nfe)
				{
					context.addWriteMessage("Lower bound of range built on "
						+ getTokenName() + " derived edge was not a number: "
						+ lowerRange);
					return null;
				}
				try
				{
					Integer.parseInt(upperRange);
				}
				catch (NumberFormatException nfe)
				{
					context.addWriteMessage("Upper bound of range built on "
						+ getTokenName() + " derived edge was not a number: "
						+ upperRange);
					return null;
				}
				sb.append(lowerRange).append('-').append(upperRange);
			}
			else if (kind.equalsIgnoreCase("HD"))
			{
				if (!PrerequisiteOperator.GTEQ.equals(prereq.getOperator()))
				{
					context.addWriteMessage("Invalid Operator built on "
						+ getTokenName() + " derived edge");
					return null;
				}
				sb.append(prereq.getOperand()).append('+');
			}
			else
			// if (!kind.equalsIgnoreCase("HD"))
			{
				context.addWriteMessage("Prerequisiste on " + getTokenName()
					+ " derived edge must be HD");
				return null;
			}
			sb.append(':');

			Collection<String> unparse = context.unparse(pctChild);
			if (unparse != null)
			{
				int masterLength = sb.length();
				for (String str : unparse)
				{
					sb.setLength(masterLength);
					set.add(sb.append(str).toString());
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMTemplate> getTokenClass()
	{
		return CDOMTemplate.class;
	}
}
