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
		
	public static Msg createCheckNameMsg(String payload) {
		return new Msg(MsgType.CHECKNAME, payload);
	}
	
	public static Msg createCheckNameReplyMsg(String payload) {
		return new Msg(MsgType.CHECKNAME_REPLY, payload);
	}
	
}
