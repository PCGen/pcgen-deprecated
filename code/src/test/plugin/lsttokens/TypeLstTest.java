package plugin.lsttokens;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PCTemplate;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTypeSafeListTestCase;

public class TypeLstTest extends AbstractGlobalTypeSafeListTestCase
{

	@Override
	public Object getConstant(String string)
	{
		return Type.getConstant(string);
	}

	@Override
	public char getJoinCharacter()
	{
		return '.';
	}

	@Override
	public ListKey<?> getListKey()
	{
		return ListKey.TYPE;
	}

	static GlobalLstToken token = new TypeLst();
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
