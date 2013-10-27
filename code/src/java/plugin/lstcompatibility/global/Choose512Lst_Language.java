package plugin.lstcompatibility.global;

import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ChooseActionContainer;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;

public class Choose512Lst_Language extends AbstractToken implements
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
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
			throws PersistenceLayerException
	{
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
		if (!token.startsWith("Language("))
		{
			// Not valid compatibility
			return false;
		}
		if (!token.endsWith(")"))
		{
			return false;
		}
		int openParenLoc = token.indexOf("(");
		String args = token.substring(openParenLoc, token.length() - 1);
		StringTokenizer ct = new StringTokenizer(args, Constants.COMMA);
		StringBuilder newArgs = new StringBuilder();
		boolean needPipe = false;
		while (ct.hasMoreTokens())
		{
			String tok = ct.nextToken();
			int dotLoc = tok.indexOf('.');
			if (dotLoc != tok.lastIndexOf('.'))
			{
				return false;
			}
			if (needPipe)
			{
				newArgs.append('|');
			}
			needPipe = true;
			newArgs.append("TYPE=");
			if (dotLoc == -1)
			{
				newArgs.append(tok);
			}
			else
			{
				newArgs.append(tok.substring(0, dotLoc));
				newArgs.append(Constants.COMMA);
				newArgs.append(tok.substring(dotLoc + 1));
			}
		}

		PrimitiveChoiceSet<CDOMLanguage> pcs = context.getChoiceSet(
				CDOMLanguage.class, newArgs.toString());
		Formula maxFormula = maxCount == null ? FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE) : FormulaFactory
				.getFormulaFor(maxCount);
		Formula countFormula = count == null ? FormulaFactory
				.getFormulaFor("1") : FormulaFactory.getFormulaFor(count);
		ChoiceSet<CDOMLanguage> chooser = new ChoiceSet<CDOMLanguage>("Choose",
				pcs);
		ChooseActionContainer container = cdo.getChooseContainer();
		container.setChoiceSet(chooser);
		container.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		return true;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
