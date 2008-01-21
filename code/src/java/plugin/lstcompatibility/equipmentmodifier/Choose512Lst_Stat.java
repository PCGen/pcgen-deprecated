package plugin.lstcompatibility.equipmentmodifier;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.EquipmentModifierLstCompatibilityToken;

public class Choose512Lst_Stat extends AbstractToken implements
		EquipmentModifierLstCompatibilityToken
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
		return 1;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public boolean parse(LoadContext context, EquipmentModifier cdo,
			String value) throws PersistenceLayerException
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
		if (!rest.startsWith("STAT"))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(rest, "|");
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (!token.equals("STAT"))
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
		return ChooseLoader.parseEqModToken(cdo, "", "STATBONUS", val
				.toString());
	}
}
