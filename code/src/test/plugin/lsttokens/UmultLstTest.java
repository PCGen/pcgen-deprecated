package plugin.lsttokens;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalIntegerTokenTestCase;
import plugin.lsttokens.testsupport.AbstractIntegerTokenTestCase;

public class UmultLstTest extends AbstractGlobalIntegerTokenTestCase
{

	@Override
	public IntegerKey getIntegerKey()
	{
		return IntegerKey.UMULT;
	}

	@Override
	public boolean isNegativeAllowed()
	{
		return false;
	}

	@Override
	public boolean isPositiveAllowed()
	{
		return true;
	}

	@Override
	public boolean isZeroAllowed()
	{
		return false;
	}

	static GlobalLstToken token = new UmultLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

}
