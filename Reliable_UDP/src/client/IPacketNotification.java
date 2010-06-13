package client;
public interface IPacketNotification {
	void onNewConnectionReply();
	void onNameReply(Boolean nameOK, String name);
}
