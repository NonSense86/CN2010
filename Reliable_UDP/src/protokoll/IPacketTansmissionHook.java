package protokoll;

public interface IPacketTansmissionHook {
	void OnPacketReceived(RUDPPacket rudpPacket);
}
