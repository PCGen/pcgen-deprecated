package plugin.lstcompatibility.global;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

public class Choose512Lst extends AbstractToken implements
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
		return 12;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		if (obj instanceof CDOMEqMod)
		{
			return false;
		}
		int lastPipe = value.lastIndexOf('|');
		if (lastPipe == -1)
		{
			return false;
		}
		String last = value.substring(lastPipe + 1);
		try
		{
			Integer.parseInt(last);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		final String val = value.substring(0, lastPipe);

		String token = null;
		String rest = val;
		String count = null;
		String maxCount = null;
		int pipeLoc = val.indexOf(Constants.PIPE);
		while (pipeLoc != -1)
		{
			token = rest.substring(0, pipeLoc);
			rest = rest.substring(pipeLoc + 1);
			if (token.startsWith("COUNT="))
			{
				if (count != null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"Cannot use COUNT more than once in CHOOSE: "
									+ value);
					return false;
				}
				count = token.substring(6);
				if (count == null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"COUNT in CHOOSE must be a formula: " + value);
					return false;
				}
			}
			else if (token.startsWith("NUMCHOICES="))
			{
				if (maxCount != null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"Cannot use NUMCHOICES more than once in CHOOSE: "
									+ value);
					return false;
				}
				maxCount = token.substring(11);
				if (maxCount == null || maxCount.length() == 0)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"NUMCHOICES in CHOOSE must be a formula: " + value);
					return false;
				}
			}
			else
			{
				break;
			}
			pipeLoc = rest.indexOf(Constants.PIPE);
		}
		String key;
		String v;
		if (rest.startsWith("FEAT="))
		{
			key = "FEAT";
			v = rest.substring(5);
		}
		else if (pipeLoc == -1)

		{
			key = rest;
			v = null;
		}
		else
		{
			key = token;
			v = rest;
		}
		if (v != null)
		{
			int titleLoc = v.indexOf("|TITLE=");
			String title = null;
			if (titleLoc != -1)
			{
				if (v.substring(titleLoc + 1).indexOf(Constants.PIPE) != -1)
				{
					Logging
							.addParseMessage(
									Logging.LST_ERROR,
									"CHOOSE: If TITLE= is used, must END with TITLE= . "
											+ "No additional arguments allowed after the title.  "
											+ "Offending value: " + value);
					return false;
				}
				title = v.substring(titleLoc + 7);
				v = v.substring(0, titleLoc);
			}
		}

		/*
		 * TODO Need to process the title!!!
		 */
		PrimitiveChoiceSet<?> chooser = context.getChoiceSet(obj, key, v);
		if (chooser == null)
		{
			// Yes, direct access, not through the context!!
			obj.put(StringKey.CHOOSE_BACKUP, value);
			return false;
		}
		ChooseActionContainer cac = obj.getChooseContainer();
		Formula maxFormula = maxCount == null ? FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE) : FormulaFactory
				.getFormulaFor(maxCount);
		Formula countFormula = count == null ? FormulaFactory.getFormulaFor(1)
				: FormulaFactory.getFormulaFor(count);
		ChoiceSet<?> choiceSet = new ChoiceSet(Constants.CHOOSE, chooser);
		cac.setChoiceSet(choiceSet);
		cac.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		cac.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		return true;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
