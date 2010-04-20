package cmd;

import core.Server;

public interface ServerCmd {

	public void execute (Server server, String[] args);
}
