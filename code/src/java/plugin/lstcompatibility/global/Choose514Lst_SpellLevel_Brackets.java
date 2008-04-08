package plugin.lstcompatibility.global;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.CompoundOrChoiceSet;
import pcgen.cdom.helper.GrantBonusActor;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.helper.SpellLevelChoiceSet;
import pcgen.cdom.inst.CDOMSpellProgressionInfo;
import pcgen.core.Constants;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

public class Choose514Lst_SpellLevel_Brackets extends AbstractToken implements
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
		return 5;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
			throws PersistenceLayerException
	{
		String token = value;
		String rest = value;
		String maxCount = null;
		int pipeLoc = value.indexOf(Constants.PIPE);
		while (pipeLoc != -1)
		{
			token = rest.substring(0, pipeLoc);
			rest = rest.substring(pipeLoc + 1);
			if (token.startsWith("COUNT="))
			{
				Logging.errorPrint("Cannot use COUNT in CHOOSE:SPELLLEVEL: "
						+ value);
				return false;
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
		if (!token.equals("SPELLLEVEL"))
		{
			// Not valid compatibility
			return false;
		}
		List<String> list = new ArrayList<String>();
		int bracketLoc;
		while ((bracketLoc = rest.lastIndexOf('[')) != -1)
		{
			int closeLoc = rest.indexOf("]", bracketLoc);
			if (closeLoc != rest.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments does not contain matching brackets: "
						+ rest);
				return false;
			}
			String bracketString = rest.substring(bracketLoc + 1, closeLoc);
			if (bracketString.startsWith("BONUS:"))
			{
				list.add(bracketString);
			}
			else
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments may not contain [" + bracketString
						+ "] : " + value);
				return false;
			}
			rest = rest.substring(0, bracketLoc);
		}
		if (hasIllegalSeparator('|', rest))
		{
			return false;
		}

		pipeLoc = rest.indexOf("|");
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
			count = Integer.parseInt(rest.substring(0, pipeLoc));
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " first argument must be an Integer : " + value);
			return false;
		}

		StringTokenizer tok = new StringTokenizer(rest.substring(pipeLoc + 1),
				Constants.PIPE);
		if (tok.countTokens() % 3 != 0)
		{
			Logging.errorPrint("COUNT:" + getTokenName()
					+ " requires a multiple of three arguments: " + value);
			return false;
		}
		List<PrimitiveChoiceSet<String>> pcsList = new ArrayList<PrimitiveChoiceSet<String>>();
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			int equalsLoc = tokString.indexOf("=");
			if (equalsLoc == tokString.length() - 1)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " arguments must have value after = : " + tokString);
				Logging.errorPrint("  entire token was: " + value);
				return false;
			}
			if (tokString.startsWith("CLASS="))
			{
				tokString = tokString.substring(6);
			}
			else if (!tokString.startsWith("TYPE="))
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " argument must start with CLASS= or TYPE= : "
						+ tokString);
				Logging.errorPrint("  Entire Token was: " + value);
				return false;
			}
			CDOMReference<CDOMSpellProgressionInfo> slref = TokenUtilities
					.getTypeOrPrimitive(context,
							CDOMSpellProgressionInfo.class, tokString);
			Formula min;
			try
			{
				String second = tok.nextToken();
				min = FormulaFactory.getFormulaFor(Integer.parseInt(second));
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " second argument must be an Integer : " + value);
				return false;
			}
			// No validation can be performed because third is a formula :P
			Formula max = FormulaFactory.getFormulaFor(tok.nextToken());
			pcsList.add(new SpellLevelChoiceSet(slref, min, max));
		}

		PrimitiveChoiceSet<String> pcs = new CompoundOrChoiceSet<String>(
				pcsList);
		Formula maxFormula = maxCount == null ? FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE) : FormulaFactory
				.getFormulaFor(maxCount);
		Formula countFormula = FormulaFactory.getFormulaFor(count);
		ChoiceSet<String> chooser = new ChoiceSet<String>("Choose", pcs);
		ChooseActionContainer container = cdo.getChooseContainer();
		container.setChoiceSet(chooser);
		container.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		for (String s : list)
		{
			container.addActor(new GrantBonusActor(s));
		}
		return true;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
