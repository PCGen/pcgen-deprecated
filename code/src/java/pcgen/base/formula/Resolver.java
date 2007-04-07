package pcgen.base.formula;

public interface Resolver<T>
{

	public T resolve();

	public String toLSTFormat();
}
