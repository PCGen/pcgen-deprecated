package plugin.lstcompatibility.global;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ChooseActionContainer;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

public class Choose514Lst_ArmorProf extends AbstractToken implements
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
		return 2;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
			throws PersistenceLayerException
	{
		if (!value.startsWith("ARMORPROF|"))
		{
			// Not valid compatibility
			return false;
		}
		value = value.substring(10);
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
		List<CDOMReference<CDOMEquipment>> cs = new ArrayList<CDOMReference<CDOMEquipment>>();
		String rest = value.substring(pipeLoc + 1);
		if (Constants.LST_ANY.equals(rest))
		{
			cs.add(context.ref.getCDOMTypeReference(CDOMEquipment.class,
					"Armor"));
		}
		else
		{
			List<CDOMReference<CDOMEquipment>> rc = getReferenceChooser(
					context, rest);
			if (rc == null)
			{
				return false;
			}
			cs.addAll(rc);
		}
		ReferenceChoiceSet<CDOMEquipment> pcs = new ReferenceChoiceSet<CDOMEquipment>(
				cs);

		Formula maxFormula = FormulaFactory.getFormulaFor(count);
		Formula countFormula = FormulaFactory.getFormulaFor(1);
		ChoiceSet<CDOMEquipment> chooser = new ChoiceSet<CDOMEquipment>(
				"Choose", pcs);
		ChooseActionContainer container = cdo.getChooseContainer();
		container.setChoiceSet(chooser);
		container.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		return true;
	}

	private List<CDOMReference<CDOMEquipment>> getReferenceChooser(
			LoadContext context, String rest)
	{
		StringTokenizer st = new StringTokenizer(rest, Constants.PIPE);
		List<CDOMReference<CDOMEquipment>> eqList = new ArrayList<CDOMReference<CDOMEquipment>>();
		while (st.hasMoreTokens())
		{
			String tokString = st.nextToken();
			if (Constants.LST_ANY.equals(tokString))
			{
				Logging.errorPrint("In CHOOSE:" + getTokenName()
						+ ": Cannot use ANY and another qualifier: " + rest);
				return null;
			}
			else
			{
				CDOMReference<CDOMEquipment> ref;
				if (tokString.startsWith(Constants.LST_TYPE_OLD)
						|| tokString.startsWith(Constants.LST_TYPE))
				{
					ref = TokenUtilities.getTypeReference(context,
							CDOMEquipment.class, "Armor."
									+ tokString.substring(5));
				}
				else
				{
					/*
					 * TODO What if this isn't armor??
					 */
					ref = context.ref.getCDOMReference(CDOMEquipment.class,
							tokString);
				}
				if (ref == null)
				{
					Logging.errorPrint("Invalid Reference: " + tokString
							+ " in CHOOSE:" + getTokenName() + ": " + rest);
					return null;
				}
				eqList.add(ref);
			}
		}
		return eqList;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
