package cmd;

import client.ClientInstance;

public class MulticastCmd extends Command {
	
	private static final String usage = "multicast <MessageText>";
	
	public MulticastCmd() {
		super(usage);
	}

	@Override
	public void execute(ClientInstance client, String[] args) {
		if (args.length != 2) {
			System.out.println("Invalid argument count.");
			System.out.println("USAGE: " + usage);
			return;
		}
		
	}

}
