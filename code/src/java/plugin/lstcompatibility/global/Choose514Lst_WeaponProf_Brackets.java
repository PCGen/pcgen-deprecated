package plugin.lstcompatibility.global;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.actor.GrantActor;
import pcgen.cdom.actor.GrantAssociationActor;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

public class Choose514Lst_WeaponProf_Brackets extends AbstractToken implements
		CDOMCompatibilityToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "CHOOSE";
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 4;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
			throws PersistenceLayerException
	{
		if (!value.startsWith("WEAPONPROF|"))
		{
			// Not valid compatibility
			return false;
		}
		value = value.substring(11);
		if (value.indexOf(',') != -1)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " arguments may not contain , : " + value);
			return false;
		}
		boolean grantWeaponProf = false;
		List<CDOMSingleRef<CDOMAbility>> list = new ArrayList<CDOMSingleRef<CDOMAbility>>();
		int bracketLoc;
		while ((bracketLoc = value.lastIndexOf('[')) != -1)
		{
			int closeLoc = value.indexOf("]", bracketLoc);
			if (closeLoc != value.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments does not contain matching brackets: "
						+ value);
				return false;
			}
			String bracketString = value.substring(bracketLoc + 1, closeLoc);
			if ("WEAPONPROF".equals(bracketString))
			{
				// This is okay.
				grantWeaponProf = true;
			}
			else if (bracketString.startsWith("FEAT="))
			{
				list.add(context.ref.getCDOMReference(CDOMAbility.class,
						CDOMAbilityCategory.FEAT, bracketString.substring(5)));
			}
			else
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments may not contain [" + bracketString
						+ "] : " + value);
				return false;
			}
			value = value.substring(0, bracketLoc);
		}
		if (hasIllegalSeparator('|', value))
		{
			return false;
		}

		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			Logging
					.errorPrint("CHOOSE:" + getTokenName()
							+ " must have two or more | delimited arguments : "
							+ value);
			return false;
		}
		int count;
		try
		{
			count = Integer.parseInt(value.substring(0, pipeLoc));
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " first argument must be an Integer : " + value);
			return false;
		}
		String rest = value.substring(pipeLoc + 1);
		PrimitiveChoiceSet<CDOMWeaponProf> pcs = context.getChoiceSet(
				CDOMWeaponProf.class, rest);

		Formula maxFormula = FormulaFactory.getFormulaFor(count);
		Formula countFormula = FormulaFactory.getFormulaFor(count);
		ChoiceSet<CDOMWeaponProf> chooser = new ChoiceSet<CDOMWeaponProf>(
				"Choose", pcs);
		ChooseActionContainer container = cdo.getChooseContainer();
		container.setChoiceSet(chooser);
		container.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		if (grantWeaponProf)
		{
			container.addActor(new GrantActor<CDOMWeaponProf>());
		}
		for (CDOMSingleRef<CDOMAbility> ref : list)
		{
			container.addActor(new GrantAssociationActor<CDOMAbility>(ref));
		}
		return true;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
