package pcgen.base.util;

import java.util.Random;

/**
 * 
 */
public final class RandomUtil
{

	/** this is used by the random selection tools */
	private static final Random RANDOM = new Random(System.currentTimeMillis());

	private RandomUtil()
	{
		// Can't instantiate
	}

	/**
	 * Get a random int
	 * 
	 * @return random int
	 */
	public static int getRandomInt()
	{
		return RANDOM.nextInt();
	}

	/**
	 * Get a random integer between 0 (inclusive) and the given value
	 * (exclusive)
	 * 
	 * @param high
	 * @return random int
	 */
	public static int getRandomInt(final int high)
	{
		//
		// Sanity check. If 'high' is <= 0, a IllegalArgumentException will be
		// thrown
		//
		if (high <= 0)
		{
			return 0;
		}
		final int rand = RANDOM.nextInt(high);
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Generated random number between " //$NON-NLS-1$
				+ "0 and " + high + ": " + rand); //$NON-NLS-1$//$NON-NLS-2$
		}
		return rand;
	}

}
