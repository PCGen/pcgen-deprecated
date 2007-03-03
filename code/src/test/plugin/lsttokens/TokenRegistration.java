package plugin.lsttokens;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

public class TokenRegistration
{

	public static Set<String> ppiSet =
			new HashSet<String>();

	public static void register(PrerequisiteParserInterface ppi)
		throws PersistenceLayerException
	{
		String s = Arrays.asList(ppi.kindsHandled()).toString();
		if (!ppiSet.contains(s))
		{
			PreParserFactory.register(ppi);
			ppiSet.add(s);
		}
	}

	public static Set<LstToken> tokenSet = new HashSet<LstToken>();

	public static void register(LstToken token)
		throws PersistenceLayerException
	{
		if (!tokenSet.contains(token))
		{
			TokenStore.inst().addToTokenMap(token);
			tokenSet.add(token);
		}
	}
}
