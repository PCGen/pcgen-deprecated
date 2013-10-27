package plugin.lstcompatibility.global;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;

public class Auto512Lst_ShieldProf extends AbstractToken implements
		CDOMCompatibilityToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "AUTO";
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

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
			throws PersistenceLayerException
	{
		if (!value.startsWith("SHIELDPROF|"))
		{
			// Not valid compatibility
			return false;
		}
		String profs = value.substring(11);
		StringTokenizer st = new StringTokenizer(profs, "|", true);
		StringBuilder newProf = new StringBuilder();
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (token.startsWith("TYPE=") || token.startsWith("TYPE."))
			{
				String type = token.substring(5);
				newProf.append("SHIELDTYPE=").append(type);
			}
			else
			{
				newProf.append(token);
			}
		}
		return context.processSubToken(cdo, getTokenName(), "SHIELDPROF",
				newProf.toString());
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
