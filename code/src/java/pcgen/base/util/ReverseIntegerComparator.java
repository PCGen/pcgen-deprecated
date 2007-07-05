package pcgen.base.util;

import java.util.Comparator;

public class ReverseIntegerComparator implements Comparator<Integer>
{

	public int compare(Integer arg0, Integer arg1)
	{
		return -arg0.compareTo(arg1);
	}

}
