package plugin.lstcompatibility.global;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstCompatibilityToken;
import pcgen.util.Logging;

public class Choose514Lst_UserInput extends AbstractToken implements
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
		return 6;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
			throws PersistenceLayerException
	{
		if (!value.startsWith("USERINPUT|"))
		{
			// Not valid compatibility
			return false;
		}
		value = value.substring(10);
		int pipeLoc = value.indexOf("|");
		String title;
		int count = 1;
		if (pipeLoc == -1)
		{
			// Integer below (# entries) defaults to 1
			title = value;
		}
		else
		{
			String start = value.substring(0, pipeLoc);
			try
			{
				count = Integer.parseInt(start);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("CHOOSE:" + getTokenName()
						+ " first argument must be an Integer : " + value);
				return false;
			}
			title = value.substring(pipeLoc + 1);
		}
		if (!title.startsWith("TITLE=\""))
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " second argument must start with TITLE=\" : " + value);
			return false;
		}
		if (!title.endsWith("\""))
		{
			Logging.errorPrint("CHOOSE:" + getTokenName()
					+ " second argument must end with \" : " + value);
			return false;
		}

		//TODO Need to figure out how to handle this!!
		//Formula maxFormula = FormulaFactory.getFormulaFor(count);
		//Formula countFormula = FormulaFactory.getFormulaFor(count);
		//PromptActionContainer container = cdo.getPromptContainer();
		//container.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		//container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		//container.setAssociation(AssociationKey.CHOICE_TITLE, title);

		return true;
	}
}
