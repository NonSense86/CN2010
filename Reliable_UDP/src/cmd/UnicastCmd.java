package cmd;

import java.io.IOException;

import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;

import common.Msg;
import common.MsgFactory;

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
		
		if (client.getName().equals(args[1])) {
			System.out.println("You can not send a Msg to yourself.");
			return;
		}
		
		try {
			Msg msg = MsgFactory.createUnicastMsg(args[2], client.getName(), args[1]);
			RUDPPacket p = RUDPPacketFactory.createPayloadPacket(client.getServers().get(0), msg);
			client.getPacketTransmission().sendPacket(p);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}

}
