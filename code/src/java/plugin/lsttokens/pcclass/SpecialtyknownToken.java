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

/**
 * Class deals with SPECIALTYKNOWN Token
 */
public class SpecialtyknownToken implements PCClassLstToken,
		PCClassLevelLstToken
{

	public String getTokenName()
	{
		return "SPECIALTYKNOWN";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		StringTokenizer st = new StringTokenizer(value, ",");
		List<String> list = new ArrayList<String>(st.countTokens());

		while (st.hasMoreTokens())
		{
			list.add(st.nextToken());
		}

		pcclass.addSpecialtyKnown(level, list);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value,
		int level)
	{
		StringTokenizer st = new StringTokenizer(value, Constants.COMMA);

		List<String> knownList = new ArrayList<String>(st.countTokens());
		while (st.hasMoreTokens())
		{
			knownList.add(st.nextToken());
		}

		SpellProgressionInfo sp = pcc.getCDOMSpellProgression();
		sp.setSpecialtyKnown(level, knownList);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc, int level)
	{
		if (!pcc.hasCDOMSpellProgression())
		{
			return null;
		}
		SpellProgressionInfo sp = pcc.getCDOMSpellProgression();
		List<String> list = sp.getSpecialtyKnownForLevel(level);
		if (list == null || list.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(list, Constants.COMMA)};
	}
}
