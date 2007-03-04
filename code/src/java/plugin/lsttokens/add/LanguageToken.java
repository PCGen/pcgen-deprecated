package plugin.lsttokens.add;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMCompoundReference;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Slot;
import pcgen.cdom.restriction.GroupRestriction;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.util.Logging;

public class LanguageToken implements AddLstToken
{
	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	public boolean parse(PObject target, String value, int level)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		String countString;
		String items;
		if (pipeLoc == -1)
		{
			countString = "1";
			items = value;
		}
		else
		{
			if (pipeLoc != value.lastIndexOf(Constants.PIPE))
			{
				Logging.errorPrint("Syntax of ADD:" + getTokenName()
					+ " only allows one | : " + value);
				return false;
			}
			countString = value.substring(0, pipeLoc);
			items = value.substring(pipeLoc + 1);
		}
		target.addAddList(level, getTokenName() + "(" + items + ")"
			+ countString);
		return true;
	}

	public String getTokenName()
	{
		return "LANGUAGE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		int count;
		String items;
		if (pipeLoc == -1)
		{
			count = 1;
			items = value;
		}
		else
		{
			String countString = value.substring(0, pipeLoc);
			try
			{
				count = Integer.parseInt(countString);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid Count in ADD:" + getTokenName()
					+ ": " + countString);
				return false;
			}
			items = value.substring(pipeLoc + 1);
		}

		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);

		Slot<Language> slot =
				context.graph.addSlotIntoGraph(getTokenName(), obj,
					LANGUAGE_CLASS, FormulaFactory.getFormulaFor(count));
		CDOMCompoundReference<Language> cr =
				new CDOMCompoundReference<Language>(LANGUAGE_CLASS,
					getTokenName() + " items");
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (token.startsWith(Constants.LST_TYPE_OLD)
				|| token.startsWith(Constants.LST_TYPE))
			{
				String[] types = token.substring(5).split("\\.");
				cr.addReference(context.ref.getCDOMTypeReference(
					LANGUAGE_CLASS, types));
			}
			else
			{
				cr.addReference(context.ref.getCDOMReference(LANGUAGE_CLASS,
					token));
			}
		}
		slot.addSinkRestriction(new GroupRestriction<Language>(LANGUAGE_CLASS,
			cr));
		return true;
	}
}
