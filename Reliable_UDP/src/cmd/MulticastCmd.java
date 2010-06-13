package cmd;

import java.io.IOException;

import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import client.ClientInstance;

import common.Msg;
import common.MsgFactory;

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
		
		try {
			Msg msg = MsgFactory.createMulticastMsg(args[1], client.getName());
			RUDPPacket p = RUDPPacketFactory.createPayloadPacket(client.getServers().get(0), msg);
			client.getPacketTransmission().sendPacket(p);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
