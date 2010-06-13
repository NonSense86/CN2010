package common;

import java.util.List;

import protokoll.PacketTransmission;
import protokoll.RemoteMachine;

public interface IKeepAlive {
	public List<RemoteMachine> getActiveConnections();
	public PacketTransmission getPacketTransmission();
}
