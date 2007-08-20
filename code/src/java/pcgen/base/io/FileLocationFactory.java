package pcgen.base.io;

public class FileLocationFactory
{

	private int sourceLine;

	private int sourceColumn;

	private int requestCount;

	public void newFile()
	{
		sourceLine = 1;
		setColumn(1);
	}

	public void setLine(int line)
	{
		sourceLine = line;
		setColumn(1);
	}

	public void setColumn(int column)
	{
		sourceColumn = column;
		requestCount = 1;
	}

	public FileLocation getFileLocation()
	{
		return new FileLocation(sourceLine, sourceColumn, requestCount++);
	}
}
