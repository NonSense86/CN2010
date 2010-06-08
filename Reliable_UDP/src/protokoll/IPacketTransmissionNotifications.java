package protokoll;

public interface IPacketTransmissionNotifications {
	void OnPacketACKMissing(PacketTansmissionInfo info);
	void OnPacketWrongOrder();
	void OnDuplicatePacket();
}
