package common;

import java.io.Serializable;
import java.util.List;

public class Msg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3378790421244467767L;

	private MsgType msgType;
	private String payload;
	private String sender;
	private String receiver;
	private boolean available;
	private List<Msg> messages;
	
	public Msg(MsgType msgType, String payload, String sender, String receiver) {
		this.msgType = msgType;
		this.payload = payload;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public Msg(MsgType msgType, String payload, String sender) {
		this.msgType = msgType;
		this.payload = payload;
		this.sender = sender;
	}
	
	public Msg(MsgType msgType, String payload) {
		this.msgType = msgType;
		this.payload = payload;
	}

	public MsgType getMsgType() {
		return msgType;
	}

	public String getPayload() {
		return payload;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public List<Msg> getMessages() {
		return messages;
	}

	public void setMessages(List<Msg> messages) {
		this.messages = messages;
	}
	
	
}
