package cmd;

import core.Client;

public interface ClientCmd {

	/**
	 * Executes the command in args[0] with parameters starting at args[1].
	 *
	 * @param args stores command and all its parameters.
	 */
	public void execute(Client c, String[] args);
}
