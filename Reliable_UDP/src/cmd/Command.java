package cmd;

import client.ClientInstance;


public abstract class Command {

	private String usage;
	
	public Command(String usage) {
		this.usage = usage;
	}
	
	public String getUsage() {
		return usage;
	}
	
	/**
	 * Executes the command in args[0] with parameters starting at args[1].
	 *
	 * @param args stores command and all its parameters.
	 */
	abstract public void execute(ClientInstance app, String[] args);
}
