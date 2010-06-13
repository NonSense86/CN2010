package protokoll;

public interface IPacketTransmissionNotifications {
	void OnPacketACKMissing(PacketTransmissionInfo info);
	void OnPacketWrongOrder();
	void OnDuplicatePacket();
}
