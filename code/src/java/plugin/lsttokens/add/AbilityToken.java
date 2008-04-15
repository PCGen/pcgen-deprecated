/*
 * AbilityToken.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on March 20, 2007
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.lsttokens.add;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.actor.GrantActor;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.core.PObject;
import pcgen.persistence.lst.AddLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * <code>AbilityToken</code> parses ADD:ABILITY entries.
 * 
 * <p>
 * <b>Tag Name</b>: <code>ADD:ABILITY</code>|w|x|y|z,z<br />
 * <b>Variables Used (w)</b>: Count (Optional Number, Variable or Formula -
 * Number of choices granted).<br />
 * <b>Variables Used (x)</b>: Ability Category (The Ability Category this
 * ability will be added to).<br />
 * <b>Variables Used (y)</b>: Ability Nature (The nature of the added ability:
 * <tt>NORMAL</tt>, <tt>AUTOMATIC</tt>, or <tt>VIRTUAL</tt>)<br />
 * <b>Variables Used (z)</b>: Ability Key or TYPE(The Ability to add. Can have
 * choices specified in &quot;()&quot;)<br />
 * <p />
 * <b>What it does:</b><br/>
 * <ul>
 * <li>Adds an Ability to a character, providing choices if these are required.</li>
 * <li>The Ability is added to the Ability Category specified.</li>
 * <li>Choices can be specified by including them in parenthesis after the
 * ability key name (whitespace is ignored).</li>
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2007-03-25 08:09:48 -0400
 * (Sun, 25 Mar 2007) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class AbilityToken extends AbstractToken implements AddLstToken,
		CDOMSecondaryToken<CDOMObject>
{
	private static final Class<CDOMAbility> ABILITY_CLASS = CDOMAbility.class;

	public String getParentToken()
	{
		return "ADD";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.AddLstToken#parse(pcgen.core.PObject,
	 *      java.lang.String, int)
	 */
	public boolean parse(PObject target, String value, int level)
	{
		String[] tokens = value.split("\\|");

		String countString;
		int index = 0;

		if (tokens.length < 3)
		{
			Logging.errorPrint("Syntax of ADD:" + getTokenName()
					+ " only allows three or four | : " + value);
			return false;
		}
		try
		{
			Integer.parseInt(tokens[0]);
			countString = tokens[0];
			index++;
			if (tokens.length != 4)
			{
				Logging.errorPrint("Syntax of ADD:" + getTokenName()
						+ " requires four | when a count is present: " + value);
				return false;
			}
		}
		catch (Exception e)
		{
			countString = "1";
			if (tokens.length != 3)
			{
				Logging.errorPrint("Syntax of ADD:" + getTokenName()
						+ " requires three | when a count is not present: "
						+ value);
				return false;
			}
		}

		// Category, nature, abilities
		String category = tokens[index++];
		if (category == null)
		{
			Logging.errorPrint("Malformed ADD Token: Missing Category: "
					+ value);
			return false;
		}
		String nature = tokens[index++];
		if (nature == null)
		{
			Logging.errorPrint("Malformed ADD Token: Missing Nature: " + value);
			return false;
		}
		String abilities = tokens[index++];

		if (abilities == null)
		{
			Logging.errorPrint("Malformed ADD Token: Missing Abilities: "
					+ value);
			return false;
		}

		StringBuffer addString = new StringBuffer();
		addString.append(getTokenName());
		addString.append("(CATEGORY=");
		addString.append(category);
		addString.append(",NATURE=");
		addString.append(nature);
		addString.append(",");
		addString.append(abilities);
		addString.append(")");
		addString.append(countString);
		target.addAddList(level, addString.toString());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "ABILITY";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);

		String nextToken = st.nextToken();
		int count;
		try
		{
			count = Integer.parseInt(nextToken);
			if (count < 1)
			{
				Logging
						.errorPrint("Count in " + getFullName()
								+ " must be > 0");
				return false;
			}
			if (!st.hasMoreTokens())
			{
				Logging.errorPrint("Invalid " + getFullName()
						+ ": has only a Count: " + value);
				return false;
			}
			nextToken = st.nextToken();
		}
		catch (NumberFormatException e)
		{
			count = 1;
			// This is OK, count is optional
		}
		// 2 left, because the first was already fetched
		if (st.countTokens() != 2)
		{
			Logging.errorPrint("Invalid " + getFullName()
					+ ": does not have the proper number of arguments: "
					+ value);
			return false;
		}

		CDOMAbilityCategory ac;
		try
		{
			ac = CDOMAbilityCategory.valueOf(nextToken);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Ability Category in " + getFullName()
					+ ": " + value);
			return false;
		}

		AbilityNature nat;
		try
		{
			nat = AbilityNature.valueOf(st.nextToken());
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Ability Nature in " + getFullName()
					+ ": " + value);
			return false;
		}

		String items = st.nextToken();
		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);

		List<CDOMReference<CDOMAbility>> refs = new ArrayList<CDOMReference<CDOMAbility>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<CDOMAbility> ref;
			if (Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(ABILITY_CLASS, ac);
			}
			else
			{
				foundOther = true;
				ref = TokenUtilities.getTypeOrPrimitive(context, ABILITY_CLASS,
						ac, token);
				if (ref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getFullName() + ": " + token
							+ " is not a valid reference: " + value);
					return false;
				}
			}
			refs.add(ref);
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ChooseActionContainer container = new ChooseActionContainer("ADD");
		container.addActor(new GrantActor<CDOMAbility>());
		context.getObjectContext().give(getFullName(), obj, container);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
				.getFormulaFor(count));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE));
		container.setAssociation(AssociationKey.ABILITY_CATEGORY, ac);
		container.setAssociation(AssociationKey.ABILITY_NATURE, nat);
		ReferenceChoiceSet<CDOMAbility> rcs = new ReferenceChoiceSet<CDOMAbility>(
				refs);
		ChoiceSet<CDOMAbility> cs = new ChoiceSet<CDOMAbility>("ADD", rcs);
		container.setChoiceSet(cs);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<ChooseActionContainer> grantChanges = context
				.getObjectContext().getGivenChanges(getFullName(), obj,
						ChooseActionContainer.class);
		Collection<ChooseActionContainer> addedItems = grantChanges.getAdded();
		if (addedItems == null || addedItems.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (LSTWriteable lstw : addedItems)
		{
			ChooseActionContainer container = (ChooseActionContainer) lstw;
			if (!"ADD".equals(container.getName()))
			{
				context.addWriteMessage("Unexpected CHOOSE container found: "
						+ container.getName());
				continue;
			}
			ChoiceSet<?> cs = container.getChoiceSet();
			if (ABILITY_CLASS.equals(cs.getChoiceClass()))
			{
				AbilityNature nat = container
						.getAssociation(AssociationKey.ABILITY_NATURE);
				if (nat == null)
				{
					context
							.addWriteMessage("Unable to find Nature for GrantFactory");
					return null;
				}
				CDOMAbilityCategory cat = container
						.getAssociation(AssociationKey.ABILITY_CATEGORY);
				if (cat == null)
				{
					context
							.addWriteMessage("Unable to find Category for GrantFactory");
					return null;
				}
				Formula f = container
						.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName()
							+ " Count");
					return null;
				}
				String fString = f.toString();
				StringBuilder sb = new StringBuilder();
				if (!"1".equals(fString))
				{
					sb.append(fString).append(Constants.PIPE);
				}
				sb.append(cat).append(Constants.PIPE);
				sb.append(nat).append(Constants.PIPE);
				sb.append(cs.getLSTformat());
				addStrings.add(sb.toString());

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
