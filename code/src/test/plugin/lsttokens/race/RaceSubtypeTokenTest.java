package plugin.lsttokens.race;

import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.Race;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.RaceLoader;
import plugin.lsttokens.AbstractTypeSafeListTestCase;

public class RaceSubtypeTokenTest extends AbstractTypeSafeListTestCase<Race>
{

	static RacesubtypeToken token = new RacesubtypeToken();
	static RaceLoader loader = new RaceLoader();

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public LstObjectFileLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Race> getToken()
	{
		return token;
	}

	@Override
	public Object getConstant(String string)
	{
		return RaceSubType.getConstant(string);
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public ListKey<?> getListKey()
	{
		return ListKey.RACESUBTYPE;
	}

	@Test
	public void dummyTest()
	{
		//Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}
}
