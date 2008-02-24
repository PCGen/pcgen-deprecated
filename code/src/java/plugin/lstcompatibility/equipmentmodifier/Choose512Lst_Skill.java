package plugin.lstcompatibility.equipmentmodifier;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;

public class Choose512Lst_Skill extends AbstractToken implements
		CDOMCompatibilityToken<CDOMEqMod>
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

	public boolean parse(LoadContext context, CDOMEqMod cdo, String value)
			throws PersistenceLayerException
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			return false;
		}
		String start = value.substring(0, pipeLoc);
		if (ChooseLoader.isEqModChooseToken(start))
		{
			return false;
		}
		String rest = value.substring(pipeLoc + 1);
		StringBuilder val = new StringBuilder();
		val.append("TITLE=" + start);
		if (!rest.startsWith("SKILL"))
		{
			return false;
		}
		if (rest.length() == 5)
		{
			return false;// return ChooseLoader.parseEqModToken(cdo, "",
							// "SKILL", val.toString());
		}
		StringTokenizer tok = new StringTokenizer(rest, "|");
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (!token.equals("SKILL"))
			{
				val.append('|');
				if (token.startsWith("MIN=") || token.startsWith("MAX="))
				{
					val.append(token);
				}
				else
				{
					return false;
				}
			}
		}
		return false;// return ChooseLoader.parseEqModToken(cdo, "",
						// "SKILLBONUS", val.toString());
	}

	public Class<CDOMEqMod> getTokenClass()
	{
		return CDOMEqMod.class;
	}
}
