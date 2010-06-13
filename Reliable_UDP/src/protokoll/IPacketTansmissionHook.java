package protokoll;

public interface IPacketTansmissionHook {
	boolean onPacketReceived(RUDPPacket rudpPacket);
}
