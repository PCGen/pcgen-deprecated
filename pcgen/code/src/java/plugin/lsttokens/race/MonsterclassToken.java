package plugin.lsttokens.race;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MONSTERCLASS Token
 */
public class MonsterclassToken extends AbstractToken implements
		CDOMPrimaryToken<Race>
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

	@Override
	public String getTokenName()
	{
		return "MONSTERCLASS";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		int colonLoc = value.indexOf(Constants.COLON);
		if (colonLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " must have only a colon: "
					+ value);
			return false;
		}
		if (colonLoc != value.lastIndexOf(Constants.COLON))
		{
			Logging.errorPrint(getTokenName() + " must have only one colon: "
					+ value);
			return false;
		}
		String classString = value.substring(0, colonLoc);
		CDOMSingleRef<PCClass> cl = context.ref.getCDOMReference(PCCLASS_CLASS,
				classString);
		try
		{
			String numLevels = value.substring(colonLoc + 1);
			int lvls = Integer.parseInt(numLevels);
			if (lvls <= 0)
			{
				Logging.errorPrint("Number of levels in " + getTokenName()
						+ " must be greater than zero: " + value);
				return false;
			}
			LevelCommandFactory cf = new LevelCommandFactory(cl, FormulaFactory
					.getFormulaFor(lvls));
			context.getObjectContext().put(race, ObjectKey.MONSTER_CLASS, cf);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Number of levels in " + getTokenName()
					+ " must be an integer greater than zero: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, Race race)
	{
		LevelCommandFactory lcf = context.getObjectContext().getObject(race,
				ObjectKey.MONSTER_CLASS);
		if (lcf == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(lcf.getLSTformat()).append(Constants.COLON).append(
				lcf.getLevelCount().toString());
		return new String[] { sb.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}
