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
		
	}

}
