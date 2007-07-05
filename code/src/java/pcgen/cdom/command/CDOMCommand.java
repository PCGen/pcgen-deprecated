package pcgen.cdom.command;

import java.net.URI;

import pcgen.base.lang.Command;

public interface CDOMCommand extends Command
{
	public URI getSourceURI();

	public void setSourceURI(URI uri);
}
