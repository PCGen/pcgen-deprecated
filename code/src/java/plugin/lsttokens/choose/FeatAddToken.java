/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.choose;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.GrantActor;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.util.Logging;

public class FeatAddToken extends AbstractToken implements ChooseLstToken
{

	public boolean parse(PObject po, String prefix, String value)
	{
		if (value == null)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " requires additional arguments");
			return false;
		}
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain , : " + value);
			return false;
		}
		if (value.indexOf('[') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain [] : " + value);
			return false;
		}
		if (hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			int equalsLoc = tokString.indexOf("=");
			if (equalsLoc == tokString.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments must have value after = : " + tokString);
				Logging.errorPrint("  entire token was: " + value);
				return false;
			}
		}
		StringBuilder sb = new StringBuilder();
		if (prefix.length() > 0)
		{
			sb.append(prefix).append('|');
		}
		sb.append(getTokenName()).append('|').append(value);
		po.setChoiceString(sb.toString());
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "FEATADD";
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public PrimitiveChoiceSet<?> parse(LoadContext context, CDOMObject cdo,
			String value) throws PersistenceLayerException
	{
		/*
		 * TODO This can't be a ChoiceSetCompatibilityToken because it needs to do funky stuff
		 */
		ChooseActionContainer container = cdo.getChooseContainer();
		GrantActor<CDOMAbility> grantActor = new GrantActor<CDOMAbility>();
		container.addActor(grantActor);
		grantActor.setAssociation(AssociationKey.ABILITY_CATEGORY,
				CDOMAbilityCategory.FEAT);
		grantActor.setAssociation(AssociationKey.ABILITY_NATURE,
				AbilityNature.NORMAL);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
				.getFormulaFor(1));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
				.getFormulaFor(1));
		PrimitiveChoiceSet<CDOMAbility> pcs = context.getChoiceSet(
				CDOMAbility.class, "QUALIFIED[" + value + "]");
		ChoiceSet<CDOMAbility> cs = new ChoiceSet<CDOMAbility>("ADD", pcs);
		container.setChoiceSet(cs);
		context.getObjectContext().give(getTokenName(), cdo, container);
		return pcs;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
