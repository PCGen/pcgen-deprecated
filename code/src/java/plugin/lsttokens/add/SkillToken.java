package plugin.lsttokens.add;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMCompoundReference;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Slot;
import pcgen.cdom.restriction.GroupRestriction;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.util.Logging;

public class SkillToken implements AddLstToken
{

	private static final Class<Skill> SKILL_CLASS = Skill.class;

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
		return "SKILL";
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

		Slot<Skill> slot =
				context.graph.addSlotIntoGraph(getTokenName(), obj,
					SKILL_CLASS, FormulaFactory.getFormulaFor(count));
		CDOMCompoundReference<Skill> cr =
				new CDOMCompoundReference<Skill>(SKILL_CLASS, getTokenName()
					+ " items");
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (token.startsWith(Constants.LST_TYPE_OLD)
				|| token.startsWith(Constants.LST_TYPE))
			{
				String[] types = token.substring(5).split("\\.");
				cr.addReference(context.ref.getCDOMTypeReference(SKILL_CLASS,
					types));
			}
			else
			{
				cr.addReference(context.ref
					.getCDOMReference(SKILL_CLASS, token));
			}
		}
		slot.addSinkRestriction(new GroupRestriction<Skill>(SKILL_CLASS, cr));

		return true;
	}
}
