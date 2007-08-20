package pcgen.base.io;

public class FileLocation
{

	private final int sourceLine;

	private final int sourceColumn;

	private final int requestCount;

	public FileLocation(int line, int column, int count)
	{
		sourceLine = line;
		sourceColumn = column;
		requestCount = count;
	}

	@Override
	public int hashCode()
	{
		return sourceLine * 29 ^ sourceColumn * 23 ^ requestCount;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof FileLocation)
		{
			FileLocation other = (FileLocation) o;
			return sourceLine == other.sourceLine
				&& sourceColumn == other.sourceColumn
				&& requestCount == other.requestCount;
		}
		return false;
	}
}
