package common;

public class MsgFactory {

	public static Msg createUnicastMsg(String payload, String name) {
		return new Msg(MsgType.UNICAST, payload, name);
	}
	
	public static Msg createMultiCastMsg(String payload, String name) {
		return new Msg(MsgType.MULTICAST, payload, name);
	}
	
	public static Msg createRenameMsg(String payload, String name) {
		return new Msg(MsgType.RENAME, payload, name);
	}
	
	public static Msg createTxtMsg(String payload, String name) {
		return new Msg(MsgType.TXT, payload, name);
	}
	
	public static Msg createCheckNameMsg(String payload, String name) {
		return new Msg(MsgType.CHECKNAME, payload, name);
	}
	
}
