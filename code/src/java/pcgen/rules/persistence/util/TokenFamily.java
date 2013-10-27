package pcgen.rules.persistence.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.TripleKeyMap;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ChoiceSetToken;

public class TokenFamily implements Comparable<TokenFamily>
{

	public static final TokenFamily CURRENT = new TokenFamily(new Revision(
			Integer.MAX_VALUE, 0, 0));

	public static final TokenFamily REV514 = new TokenFamily(new Revision(5,
			14, Integer.MIN_VALUE));

	/**
	 * The Map of Constants
	 */
	private static SortedMap<Revision, TokenFamily> typeMap;

	/**
	 * The name of this Constant
	 */
	private final Revision rev;

	private final DoubleKeyMap<Class<?>, String, CDOMToken<?>> tokenMap = new DoubleKeyMap<Class<?>, String, CDOMToken<?>>();

	private static final TripleKeyMap<Class<?>, String, String, CDOMSubToken<?>> subTokenMap = new TripleKeyMap<Class<?>, String, String, CDOMSubToken<?>>();

	private final DoubleKeyMap<Class<?>, String, ChoiceSetToken> chooseTokenMap = new DoubleKeyMap<Class<?>, String, ChoiceSetToken>();

	private final Map<CaseInsensitiveString, PrerequisiteParserInterface> preTokenMap = new HashMap<CaseInsensitiveString, PrerequisiteParserInterface>();

	private TokenFamily(Revision r)
	{
		rev = r;
	}

	public <T> CDOMToken<T> putToken(CDOMToken<T> tok)
	{
		return (CDOMToken<T>) tokenMap.put(tok.getTokenClass(), tok
				.getTokenName(), tok);
	}

	public CDOMToken<?> getToken(Class<?> cl, String name)
	{
		return tokenMap.get(cl, name);
	}

	public Set<CDOMToken<?>> getTokens(Class<?> cl)
	{
		return tokenMap.values(cl);
	}

	public <U, T extends CDOMSubToken<U>> void putSubToken(T tok)
	{
		subTokenMap.put(tok.getTokenClass(), tok.getParentToken(), tok
				.getTokenName(), tok);
	}

	public <T> CDOMSubToken<T> getSubToken(Class<? extends T> cl, String token,
			String key)
	{
		return (CDOMSubToken<T>) subTokenMap.get(cl, token, key);
	}

	public Set<CDOMSubToken<?>> getSubTokens(Class<?> cl, String token)
	{
		return subTokenMap.values(cl, token);
	}

	public void putChooseToken(ChoiceSetToken<?> token)
	{
		chooseTokenMap.put(token.getTokenClass(), token.getTokenName(), token);
	}

	public <T> ChoiceSetToken<T> getChooseToken(Class<T> cl, String key)
	{
		return chooseTokenMap.get(cl, key);
	}

	public void putPrerequisiteToken(PrerequisiteParserInterface token)
	{
		for (String s : token.kindsHandled())
		{
			preTokenMap.put(new CaseInsensitiveString(s), token);
		}
	}

	public PrerequisiteParserInterface getPrerequisiteToken(String key)
	{
		return preTokenMap.get(new CaseInsensitiveString(key));
	}

	/**
	 * Constructs a new SequencedConstant with the given String as the Constant
	 * name and the given integer as the Sequence
	 * 
	 * @param s
	 *            The String to be used as the SequencedConstants name
	 * @return The new SequencedConstant built with the given name and sequence
	 *         number
	 */
	public static TokenFamily getConstant(int primary, int secondary,
			int tertiary)
	{
		if (typeMap == null)
		{
			buildMap();
		}
		Revision r = new Revision(primary, secondary, tertiary);
		TokenFamily o = typeMap.get(r);
		if (o == null)
		{
			o = new TokenFamily(r);
			typeMap.put(r, o);
		}
		return o;
	}

	/**
	 * Actually build the set of Constants, using any "public static final"
	 * constants within the child (extending) class as initial values in the
	 * Constant pool.
	 */
	private static void buildMap()
	{
		typeMap = new TreeMap<Revision, TokenFamily>();
		Class<TokenFamily> cl = TokenFamily.class;
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
					&& Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (cl.equals(o.getClass()))
					{
						TokenFamily tObj = cl.cast(o);
						if (typeMap.containsKey(tObj.rev))
						{
							throw new UnreachableError(
									"Attempt to redefine constant value "
											+ tObj.rev + " to "
											+ fields[i].getName()
											+ ", value was "
											+ typeMap.get(tObj.rev));
						}
						typeMap.put(tObj.rev, tObj);
					}
				}
				catch (IllegalArgumentException e)
				{
					throw new InternalError();
				}
				catch (IllegalAccessException e)
				{
					throw new InternalError();
				}
			}
		}
	}

	/**
	 * Clears all of the Constants defined by this class. Note that this does
	 * not remove any Constants declared in the Constant class (as those are
	 * considered 'permanent' members of the Sequenced Constant collection.
	 * 
	 * Note that this *will not* reset the ordinal count, because that is a
	 * dangerous operation. As there could be outstanding references to
	 * constants that would be removed from the Constant pool, no reuse of
	 * ordinals is driven by this method. As a result, calling this method may
	 * result in a Constant Pool which does not have sequentially numbered
	 * ordinal values.
	 */
	public static void clearConstants()
	{
		buildMap();
	}

	/**
	 * Returns a Collection of all of the Constants for this class. The returned
	 * Collection is unmodifiable.
	 * 
	 * @return an unmodifiable Collection of all of the Constants for this class
	 */
	public static Collection<TokenFamily> getAllConstants()
	{
		return Collections.unmodifiableCollection(typeMap.values());
	}

	public int compareTo(TokenFamily tf)
	{
		return rev.compareTo(tf.rev);
	}

	/*
	 * Note there is no reason to do .hashCode or .equals because this is Type
	 * Safe (meaning it can only build one object per Revision)
	 */

	@Override
	public String toString()
	{
		return "Token Family: " + rev.toString();
	}
}
