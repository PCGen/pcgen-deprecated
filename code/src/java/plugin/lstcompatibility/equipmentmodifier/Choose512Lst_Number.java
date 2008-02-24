package plugin.lstcompatibility.equipmentmodifier;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;

public class Choose512Lst_Number extends AbstractToken implements
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
		return 2;
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
		StringTokenizer tok = new StringTokenizer(rest, "|");
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (token.startsWith("MIN=") || token.startsWith("MAX=")
					|| token.startsWith("INCREMENT=")
					|| token.startsWith("NOSIGN")
					|| token.startsWith("MULTIPLE"))
			{
				val.append(token);
			}
			else
			{
				try
				{
					Integer.parseInt(token);
					val.append(token);
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			}
			val.append('|');
		}
		val.append("TITLE=" + start);
		return false;// return ChooseLoader.parseEqModToken(cdo, "",
						// "NUMBER", val.toString());
	}

	public Class<CDOMEqMod> getTokenClass()
	{
		return CDOMEqMod.class;
	}
}
