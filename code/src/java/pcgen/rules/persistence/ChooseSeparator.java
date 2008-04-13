package pcgen.rules.persistence;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChooseSeparator implements Iterator<String>
{

	private final String base;
	private final char sep;
	private int startIndex = 0;
	private int checkIndex = 0;
	private int openCount = 0;
	private int closeCount = 0;

	public ChooseSeparator(String chooseString, char separator)
	{
		if (chooseString == null)
		{
			throw new IllegalArgumentException(
					"Choose Separator cannot take null initialization String");
		}
		sep = separator;
		base = chooseString;
	}

	public boolean hasNext()
	{
		return startIndex < base.length();
	}

	public String next()
	{
		if (!hasNext())
		{
			throw new NoSuchElementException();
		}
		int pipeLoc = base.indexOf(sep, startIndex);
		String working;
		int trackIndex = startIndex;
		if (pipeLoc == -1)
		{
			working = base;
			pipeLoc = base.length();
		}
		else
		{
			working = base.substring(0, pipeLoc);
		}
		do
		{
			checkIndex = trackIndex;
			while (checkIndex < pipeLoc)
			{
				workOn(working.substring(checkIndex, pipeLoc));
			}
			trackIndex = pipeLoc + 1;
		} while (openCount != closeCount && trackIndex < working.length());
		if (openCount != closeCount)
		{
			throw new BracketMismatchException(base
					+ " did not have matching brackets");
		}
		String ret = working.substring(startIndex, pipeLoc);
		startIndex = trackIndex;
		return ret;
	}

	private void workOn(String working)
	{
		checkIndex++;
		int openLoc = working.indexOf('[');
		int closeLoc = working.indexOf(']');
		if (openLoc == -1)
		{
			if (closeLoc == -1)
			{
				// Indicate done
				checkIndex = Integer.MAX_VALUE;
			}
			else
			{
				// muse use close
				closeCount++;
				checkIndex += closeLoc;
			}
		}
		else if (closeLoc == -1 || openLoc < closeLoc)
		{
			openCount++;
			checkIndex += openLoc;
		}
		else
		{
			// closeLoc > openLoc
			closeCount++;
			checkIndex += closeLoc;
		}
		if (openCount < closeCount)
		{
			throw new BracketMismatchException(base + " had close before open");
		}
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	public class BracketMismatchException extends IllegalStateException
	{

		public BracketMismatchException(String base)
		{
			super(base);
		}

	}
}
