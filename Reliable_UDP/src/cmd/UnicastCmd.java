package cmd;

import client.ClientInstance;

public class UnicastCmd extends Command {

	private static final String usage = "unicast <ReceiverName> <MessageText>";
	
	public UnicastCmd() {
		super(usage);
	}

	@Override
	public void execute(ClientInstance client, String[] args) {
		if (args.length != 3) {
			System.out.println("Invalid argument count.");
			System.out.println("USAGE: " + usage);
			return;
		}
		
		
		
	}

}
