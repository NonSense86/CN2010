package cmd;

import java.io.IOException;

import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import client.ClientInstance;

import common.Msg;
import common.MsgFactory;

public class RenameCmd extends Command {

	private static final String usage = "rename <Name>";
	
	public RenameCmd() {
		super(usage);
	}

	@Override
	public void execute(ClientInstance client, String[] args) {
		if (args.length != 2) {
			System.out.println("Invalid argument count.");
			System.out.println("USAGE: " + usage);
			return;
		}
		
		if(client.getName().equals(args[1])) {
			System.out.println("You already have this name set");
			return;
		}
		
		String oldName = client.getName();
		
		Msg msg = MsgFactory.createRenameMsg(args[1], client.getName());
		try {
			RUDPPacket p = RUDPPacketFactory.createPayloadPacket(client.getServers().get(0), msg);
			client.getPacketTransmission().sendPacket(p);
		} catch(IOException e) {
			e.printStackTrace();
		}
		// Wait until response
		System.out.println("Checking...");
		while(!client.isNameChecked()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		client.setNameChecked(false);
		if(oldName.equals(client.getName())) {
			System.out.println("The name is not available");
		} else {
			System.out.println("Name set to: " + client.getName());
		}
	}

}
