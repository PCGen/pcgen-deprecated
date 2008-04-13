package pcgen.cdom.base;

public class SourceWrapper
{

	private final PrereqObject target;
	private final String sourceToken;

	public SourceWrapper(PrereqObject pro, String source)
	{
		target = pro;
		sourceToken = source;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj instanceof SourceWrapper)
		{
			SourceWrapper other = (SourceWrapper) obj;
			return target.equals(other.target)
					&& sourceToken.equals(other.sourceToken);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return target.hashCode();
	}

	public PrereqObject getTarget()
	{
		return target;
	}

	public String getSourceToken()
	{
		return sourceToken;
	}
}
