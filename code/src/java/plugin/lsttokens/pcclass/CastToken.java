package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.core.PCClass;
import pcgen.core.SpellProgressionInfo;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCClassLevelLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CAST Token
 */
public class CastToken implements PCClassLstToken, PCClassLevelLstToken
{

	public String getTokenName()
	{
		return "CAST";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		if (level > 0)
		{
			StringTokenizer st = new StringTokenizer(value, ",");

			List<String> castList = new ArrayList<String>(st.countTokens());
			while (st.hasMoreTokens())
			{
				castList.add(st.nextToken());
			}

			pcclass.setCast(level, castList);
			return true;
		}
		Logging.errorPrint("CAST tag without level not allowed!");
		return false;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value,
		int level)
	{
		StringTokenizer st = new StringTokenizer(value, Constants.COMMA);

		List<String> castList = new ArrayList<String>(st.countTokens());
		while (st.hasMoreTokens())
		{
			castList.add(st.nextToken());
		}

		SpellProgressionInfo sp = pcc.getCDOMSpellProgression();
		sp.setCast(level, castList);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc, int level)
	{
		if (!pcc.hasCDOMSpellProgression())
		{
			return null;
		}
		SpellProgressionInfo sp = pcc.getCDOMSpellProgression();
		List<String> list = sp.getCastForLevel(level);
		if (list == null || list.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(list, Constants.COMMA)};
	}
}
