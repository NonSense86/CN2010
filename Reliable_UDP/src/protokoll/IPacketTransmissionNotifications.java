package protokoll;

public interface IPacketTransmissionNotifications {

	public void onDuplicatePacket();
	
	public void onPacketWrongOrder();
}
