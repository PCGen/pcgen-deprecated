package pcgen.cdom.helper;

public class Quality
{

	private final String quality;
	private final String value;

	public Quality(String key, String val)
	{
		quality = key;
		value = val;
	}

	public String getQuality()
	{
		return quality;
	}

	public String getValue()
	{
		return value;
	}

}
