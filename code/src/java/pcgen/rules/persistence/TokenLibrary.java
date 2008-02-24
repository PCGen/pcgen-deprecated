package pcgen.rules.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMPCClassLevel;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMCompatibilitySubToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.CDOMSubToken;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ChoiceSetCompatibilityToken;
import pcgen.rules.persistence.token.ChoiceSetToken;
import pcgen.rules.persistence.token.ChooseLstGlobalQualifierToken;
import pcgen.rules.persistence.token.ChooseLstQualifierToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.rules.persistence.util.TokenFamily;

public class TokenLibrary
{
	private static final Class<CDOMPCClass> PCCLASS_CLASS = CDOMPCClass.class;

	static final Class<CDOMObject> CDOMOBJECT_CLASS = CDOMObject.class;

	private final static List<DeferredToken<? extends CDOMObject>> deferredTokens = new ArrayList<DeferredToken<? extends CDOMObject>>();

	private final static HashMap<String, Class<ChooseLstGlobalQualifierToken<?>>> globalQualifierMap = new HashMap<String, Class<ChooseLstGlobalQualifierToken<?>>>();

	private final static DoubleKeyMap<Class<? extends CDOMObject>, String, Class<ChooseLstQualifierToken<?>>> qualifierMap = new DoubleKeyMap<Class<? extends CDOMObject>, String, Class<ChooseLstQualifierToken<?>>>();

	private final static DoubleKeyMap<Class<?>, String, Class<PrimitiveToken<?>>> primitiveMap = new DoubleKeyMap<Class<?>, String, Class<PrimitiveToken<?>>>();

	private final static Set<TokenFamily> tokenSources = new TreeSet<TokenFamily>();

	static
	{
		tokenSources.add(TokenFamily.CURRENT);
		tokenSources.add(TokenFamily.REV514);
	}

	public static <T> PrimitiveToken<T> getPrimitive(Class<T> name,
			String tokKey)
	{
		Class<PrimitiveToken<?>> cptc = primitiveMap.get(name, tokKey);
		if (cptc == null)
		{
			return null;
		}
		try
		{
			return (PrimitiveToken<T>) cptc.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError("new Instance on " + cptc
					+ " should not fail in getPrimitive", e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError("new Instance on " + cptc
					+ " should not fail due to access", e);
		}
	}

	public static <T extends CDOMObject> ChooseLstQualifierToken<T> getChooseQualifier(
			Class<T> domain_class, String key)
	{
		Class<ChooseLstQualifierToken<?>> clqtc = qualifierMap.get(
				domain_class, key);
		if (clqtc == null)
		{
			return null;
		}
		try
		{
			return (ChooseLstQualifierToken<T>) clqtc.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError("new Instance on " + clqtc
					+ " should not fail in getChooseQualifier", e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError("new Instance on " + clqtc
					+ " should not fail due to access", e);
		}
	}

	public static <T extends CDOMObject> ChooseLstGlobalQualifierToken<T> getGlobalChooseQualifier(
			String key)
	{
		Class<ChooseLstGlobalQualifierToken<?>> clgqtc = globalQualifierMap
				.get(key);
		if (clgqtc == null)
		{
			return null;
		}
		try
		{
			return (ChooseLstGlobalQualifierToken<T>) clgqtc.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError("new Instance on " + clgqtc
					+ " should not fail in getGlobalChooseQualifier", e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError("new Instance on " + clgqtc
					+ " should not fail due to access", e);
		}
	}

	public static List<DeferredToken<? extends CDOMObject>> getDeferredTokens()
	{
		return new ArrayList<DeferredToken<? extends CDOMObject>>(
				deferredTokens);
	}

	public static void addToPrimitiveMap(PrimitiveToken<?> p)
	{
		Class<? extends PrimitiveToken> newTokClass = p.getClass();
		if (PrimitiveToken.class.isAssignableFrom(newTokClass))
		{
			primitiveMap.put(((PrimitiveToken) p).getReferenceClass(), p
					.getTokenName(), (Class<PrimitiveToken<?>>) newTokClass);
		}
	}

	public static void addToQualifierMap(QualifierToken<?> p)
	{
		Class<? extends QualifierToken> newTokClass = p.getClass();
		if (ChooseLstQualifierToken.class.isAssignableFrom(newTokClass))
		{
			qualifierMap.put(((ChooseLstQualifierToken<?>) p).getChoiceClass(),
					p.getTokenName(),
					(Class<ChooseLstQualifierToken<?>>) newTokClass);
		}
		if (ChooseLstGlobalQualifierToken.class.isAssignableFrom(newTokClass))
		{
			globalQualifierMap.put(p.getTokenName(),
					(Class<ChooseLstGlobalQualifierToken<?>>) newTokClass);
		}
	}

	public static void addToTokenMap(Object newToken)
	{
		if (newToken instanceof DeferredToken)
		{
			deferredTokens.add((DeferredToken<?>) newToken);
		}
		if (newToken instanceof CDOMPrimaryToken)
		{
			CDOMPrimaryToken<?> tok = (CDOMPrimaryToken<?>) newToken;
			TokenFamily.CURRENT.putToken(tok);
			if (PCCLASS_CLASS.equals(tok.getTokenClass()))
			{
				addToTokenMap(new ClassWrappedToken(
						(CDOMPrimaryToken<CDOMPCClass>) tok));
			}
		}
		if (newToken instanceof CDOMSecondaryToken)
		{
			TokenFamily.CURRENT.putSubToken((CDOMSecondaryToken<?>) newToken);
		}
		if (newToken instanceof ChoiceSetToken)
		{
			TokenFamily.CURRENT.putChooseToken((ChoiceSetToken<?>) newToken);
		}
		if (newToken instanceof PrerequisiteParserInterface)
		{
			PrerequisiteParserInterface prereqToken = (PrerequisiteParserInterface) newToken;
			TokenFamily.CURRENT.putPrerequisiteToken(prereqToken);
			for (String s : prereqToken.kindsHandled())
			{
				PreCompatibilityToken pos = new PreCompatibilityToken(s,
						prereqToken, false);
				TokenFamily.REV514.putToken(pos);
				TokenFamily.REV514.putSubToken(pos);
				PreCompatibilityToken neg = new PreCompatibilityToken(s,
						prereqToken, true);
				TokenFamily.REV514.putToken(neg);
				TokenFamily.REV514.putSubToken(neg);
			}
		}
		if (newToken instanceof CDOMCompatibilityToken)
		{
			CDOMCompatibilityToken<?> tok = (CDOMCompatibilityToken<?>) newToken;
			TokenFamily fam = TokenFamily.getConstant(tok.compatibilityLevel(),
					tok.compatibilitySubLevel(), tok.compatibilityPriority());
			fam.putToken(tok);
			tokenSources.add(fam);
			if (fam.compareTo(TokenFamily.REV514) <= 0
					&& PCCLASS_CLASS.equals(tok.getTokenClass()))
			{
				addToTokenMap(new ClassWrappedToken(
						(CDOMCompatibilityToken<CDOMPCClass>) tok));
			}
		}
		if (newToken instanceof CDOMCompatibilitySubToken)
		{
			CDOMCompatibilitySubToken<?> tok = (CDOMCompatibilitySubToken<?>) newToken;
			TokenFamily fam = TokenFamily.getConstant(tok.compatibilityLevel(),
					tok.compatibilitySubLevel(), tok.compatibilityPriority());
			fam.putSubToken(tok);
			tokenSources.add(fam);
		}
		if (newToken instanceof ChoiceSetCompatibilityToken)
		{
			ChoiceSetCompatibilityToken tok = (ChoiceSetCompatibilityToken) newToken;
			TokenFamily fam = TokenFamily.getConstant(tok.compatibilityLevel(),
					tok.compatibilitySubLevel(), tok.compatibilityPriority());
			fam.putChooseToken(tok);
			tokenSources.add(fam);
		}
	}

	abstract static class AbstractTokenIterator<C, T> implements Iterator<T>
	{
		private static final Class<Object> OBJECT_CLASS = Object.class;
		private final Class<C> rootClass;
		private final String tokenKey;
		private T nextToken = null;
		private boolean needNewToken = true;
		private Class<?> stopClass;
		private final Iterator<TokenFamily> subIterator;

		public AbstractTokenIterator(Class<C> cl, String key)
		{
			rootClass = cl;
			subIterator = tokenSources.iterator();
			tokenKey = key;
		}

		public boolean hasNext()
		{
			setNextToken();
			return !needNewToken;
		}

		protected void setNextToken()
		{
			while (needNewToken && subIterator.hasNext())
			{
				TokenFamily family = subIterator.next();
				Class<?> actingClass = rootClass;
				nextToken = grabToken(family, actingClass, tokenKey);
				while (nextToken == null && actingClass != null
						&& !actingClass.equals(stopClass))
				{
					actingClass = actingClass.getSuperclass();
					nextToken = grabToken(family, actingClass, tokenKey);
				}
				if (stopClass == null)
				{
					stopClass = actingClass;
				}
				needNewToken = nextToken == null;
			}
		}

		protected abstract T grabToken(TokenFamily family, Class<?> cl,
				String key);

		public T next()
		{
			setNextToken();
			if (needNewToken)
			{
				throw new NoSuchElementException();
			}
			needNewToken = true;
			return nextToken;
		}

		public void remove()
		{
			throw new UnsupportedOperationException(
					"Iterator does not support remove");
		}
	}

	static class TokenIterator<C extends CDOMObject, T extends CDOMToken<? super C>>
			extends TokenLibrary.AbstractTokenIterator<C, T>
	{

		public TokenIterator(Class<C> cl, String key)
		{
			super(cl, key);
		}

		@Override
		protected T grabToken(TokenFamily family, Class<?> cl, String key)
		{
			return (T) family.getToken(cl, key);
		}

	}

	static class SubTokenIterator<C, T extends CDOMSubToken<? super C>> extends
			TokenLibrary.AbstractTokenIterator<C, T>
	{
		private final String subTokenKey;

		public SubTokenIterator(Class<C> cl, String key, String subKey)
		{
			super(cl, key);
			subTokenKey = subKey;
		}

		@Override
		protected T grabToken(TokenFamily family, Class<?> cl, String key)
		{
			return (T) family.getSubToken(cl, key, subTokenKey);
		}
	}

	static class ChooseTokenIterator<C extends CDOMObject> extends
			TokenLibrary.AbstractTokenIterator<C, ChoiceSetToken<?>>
	{
		public ChooseTokenIterator(Class<C> cl, String key)
		{
			super(cl, key);
		}

		@Override
		protected ChoiceSetToken<?> grabToken(TokenFamily family, Class<?> cl,
				String key)
		{
			return family.getChooseToken(cl, key);
		}
	}

	static class PreTokenIterator
			extends
			TokenLibrary.AbstractTokenIterator<CDOMObject, PrerequisiteParserInterface>
	{
		public PreTokenIterator(String key)
		{
			super(CDOMOBJECT_CLASS, key);
		}

		@Override
		protected PrerequisiteParserInterface grabToken(TokenFamily family,
				Class<?> cl, String key)
		{
			return family.getPrerequisiteToken(key);
		}
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	private static class PreCompatibilityToken implements
			CDOMCompatibilityToken<ConcretePrereqObject>,
			CDOMCompatibilitySubToken<ConcretePrereqObject>
	{

		private final String tokenRoot;
		private final String tokenName;
		private final PrerequisiteParserInterface token;
		private final boolean invert;

		public PreCompatibilityToken(String s,
				PrerequisiteParserInterface prereqToken, boolean inv)
		{
			tokenRoot = s.toUpperCase();
			token = prereqToken;
			invert = inv;
			tokenName = (invert ? "!" : "") + "PRE" + tokenRoot;
		}

		public Class<ConcretePrereqObject> getTokenClass()
		{
			return ConcretePrereqObject.class;
		}

		public boolean parse(LoadContext context, ConcretePrereqObject obj,
				String value) throws PersistenceLayerException
		{
			Prerequisite p = token.parse(tokenRoot, value, invert, false);
			if (p == null)
			{
				return false;
			}
			obj.addPrerequisite(p);
			return true;
		}

		public String getTokenName()
		{
			return tokenName;
		}

		public int compatibilityLevel()
		{
			return 5;
		}

		public int compatibilityPriority()
		{
			return 0;
		}

		public int compatibilitySubLevel()
		{
			return 14;
		}

		public String getParentToken()
		{
			return "*KITTOKEN";
		}

	}

	public static class ClassWrappedToken implements
			CDOMCompatibilityToken<CDOMPCClassLevel>
	{

		private static int wrapIndex = Integer.MIN_VALUE;

		private static final Integer ONE = Integer.valueOf(1);

		private CDOMToken<CDOMPCClass> wrappedToken;

		private int priority = wrapIndex++;

		public Class<CDOMPCClassLevel> getTokenClass()
		{
			return CDOMPCClassLevel.class;
		}

		public ClassWrappedToken(CDOMToken<CDOMPCClass> tok)
		{
			wrappedToken = tok;
		}

		public boolean parse(LoadContext context, CDOMPCClassLevel obj,
				String value) throws PersistenceLayerException
		{
			if (ONE.equals(obj.get(IntegerKey.LEVEL)))
			{
				CDOMPCClass parent = (CDOMPCClass) obj.get(ObjectKey.PARENT);
				return wrappedToken.parse(context, parent, value);
			}
			return false;
		}

		public String getTokenName()
		{
			return wrappedToken.getTokenName();
		}

		public int compatibilityLevel()
		{
			return 5;
		}

		public int compatibilityPriority()
		{
			return priority;
		}

		public int compatibilitySubLevel()
		{
			return 14;
		}

	}
}
