package plugin.lstcompatibility.global;

import java.util.ArrayList;
import java.util.Arrays;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.CollectionChoiceSet;
import pcgen.core.EquipmentModifier;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstCompatibilityToken;
import pcgen.util.Logging;

public class Choose512Lst_NoType extends AbstractToken implements
		GlobalLstCompatibilityToken
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
		return 12;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
		throws PersistenceLayerException
	{
		if (cdo instanceof EquipmentModifier)
		{
			return false;
		}
		String token = value;
		String rest = value;
		String count = null;
		String maxCount = null;
		int pipeLoc = value.indexOf(Constants.PIPE);
		while (pipeLoc != -1)
		{
			token = rest.substring(0, pipeLoc);
			rest = rest.substring(pipeLoc + 1);
			if (token.startsWith("COUNT="))
			{
				if (count != null)
				{
					Logging
						.errorPrint("Cannot use COUNT more than once in CHOOSE: "
							+ value);
					return false;
				}
				count = token.substring(6);
				if (count == null)
				{
					Logging.errorPrint("COUNT in CHOOSE must be a formula: "
						+ value);
					return false;
				}
			}
			else if (token.startsWith("NUMCHOICES="))
			{
				if (maxCount != null)
				{
					Logging
						.errorPrint("Cannot use NUMCHOICES more than once in CHOOSE: "
							+ value);
					return false;
				}
				maxCount = token.substring(11);
				if (maxCount == null || maxCount.length() == 0)
				{
					Logging
						.errorPrint("NUMCHOICES in CHOOSE must be a formula: "
							+ value);
					return false;
				}
			}
			else
			{
				break;
			}
			pipeLoc = rest.indexOf(Constants.PIPE);
		}
		
		if (ChooserUtilities.is514ChoiceSubtoken(token))
		{
			return false;
		}
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(token);
		if (rest != null)
		{
			list.addAll(Arrays.asList(rest.split("\\|")));
		}

		CollectionChoiceSet<String> ccs = new CollectionChoiceSet<String>(list);
		ChoiceSet<String> chooser = new ChoiceSet<String>("Choose", ccs);
		ChooseActionContainer container = cdo.getChooseContainer();
		container.setChoiceSet(chooser);

		Formula maxFormula =
				maxCount == null ? FormulaFactory
					.getFormulaFor(Integer.MAX_VALUE) : FormulaFactory
					.getFormulaFor(maxCount);
		Formula countFormula =
				count == null ? FormulaFactory.getFormulaFor("1")
					: FormulaFactory.getFormulaFor(count);
		container.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);

		return true;
	}
}
