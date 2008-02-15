package pcgen.cdom.base;

import java.util.List;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;

public interface CDOMObject
{
	public boolean containsKey(IntegerKey arg0);

	public Integer get(IntegerKey arg0);

	public Set<IntegerKey> getIntegerKeys();

	public boolean containsKey(StringKey arg0);

	public String get(StringKey arg0);

	public Set<StringKey> getStringKeys();

	public boolean containsKey(FormulaKey arg0);

	public Formula get(FormulaKey arg0);

	public Set<FormulaKey> getFormulaKeys();

	public boolean containsKey(VariableKey arg0);

	public Formula get(VariableKey arg0);

	public Set<VariableKey> getVariableKeys();

	public boolean containsKey(ObjectKey<?> arg0);

	public <OT> OT get(ObjectKey<OT> arg0);

	public Set<ObjectKey<?>> getObjectKeys();

	public boolean containsListFor(ListKey<?> key);

	public <T> List<T> getListFor(ListKey<T> key);

	public int getSizeOfListFor(ListKey<?> key);

	public <T> boolean containsInList(ListKey<T> key, T value);

	// public <T> T getElementInList(ListKey<T> key, int i);
	public Set<ListKey<?>> getListKeys();

	public boolean isDescPI();

	public boolean isNamePI();

	public String getDisplayName();

}
