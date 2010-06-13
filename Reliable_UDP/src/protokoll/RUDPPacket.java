package protokoll;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;


public class RUDPPacket {
	
	// Not encoded!
	private RemoteMachine sender;
	private RemoteMachine receiver;
	private int resendCount;

	public RUDPPacket() {}
	
	public RUDPPacket(DatagramPacket packet) throws IOException {
		this.sender = new RemoteMachine(packet.getAddress(), packet.getPort());
		decodePackage(packet.getData());
	}
	
	public RUDPPacket(RemoteMachine receiver) {
		this.receiver = receiver;
	}
	
	private PacketType packetType;
		
	private long checksum;

	private int seqNumber;

	private int payloadLength;

	private byte[] payload;

	/**
	 * checksum value
	 */
	public long getChecksum() {
		return checksum;
	}

	private void setChecksum(long checksum) {
		this.checksum = checksum;
	}

	/**
	 * sequence number
	 */
	public int getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(int seqNumber) {
		this.seqNumber = seqNumber;
	}

	/**
	 * payload length
	 */
	public int getPayloadLength() {
		return payloadLength;
	}

	private void setPayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
	}

	/**
	 * payload content
	 */
	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public PacketType getPacketType() {
		return packetType;
	}

	public void setPacketType(PacketType packetType) {
		this.packetType = packetType;
	}
	
	public RemoteMachine getSender() {
		return sender;
	}

	public void setSender(RemoteMachine sender) {
		this.sender = sender;
	}

	public RemoteMachine getReceiver() {
		return receiver;
	}

	public void setReceiver(RemoteMachine receiver) {
		this.receiver = receiver;
	}
	

	public int getResendCount() {
		return resendCount;
	}

	public void setResendCount(int resendCount) {
		this.resendCount = resendCount;
	}

	/**
	 * Encodes a RUDP package. The result is written in to a byte array
	 * 
	 * @return all members encoded
	 * @throws IOException
	 */
	public byte[] encodePackage() throws IOException {
		/* Payload length for this packet */
		if (getPayload() == null) {
			setPayloadLength(0);
		} else {
			setPayloadLength(getPayload().length);
		}
		
		/* CRC for this packet */
		setChecksum(RUDPPacketCRC.calculateCRCForPacket(this));

		/* write result to stream */
		ByteArrayOutputStream buffStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(buffStream);
		
		outStream.writeUTF(packetType.toString());
		outStream.writeLong(getChecksum());
		outStream.writeInt(getSeqNumber());	

		outStream.writeInt(getPayloadLength());

		if (getPayload() != null) {
			outStream.write(getPayload());
		}

		outStream.flush();

		return buffStream.toByteArray();
	}

	public void decodePackage(byte[] packagePayload) throws IOException {
		ByteArrayInputStream buffStream = new ByteArrayInputStream(
				packagePayload);
		DataInputStream inStream = new DataInputStream(buffStream);

		
		setPacketType(PacketType.valueOf(inStream.readUTF()));
		setChecksum(inStream.readLong());
		setSeqNumber(inStream.readInt());			

		setPayloadLength(inStream.readInt());

		/* read the payload chunkwise - important if the payload is huge */
		ByteArrayOutputStream sb = new ByteArrayOutputStream();

		byte[] buffer = new byte[2048];
		int n;
		int total = 0;

		int maxread = getPayloadLength() < buffer.length ? getPayloadLength()
				: buffer.length;

		while ((n = inStream.read(buffer, 0, maxread)) >= 0) {
			sb.write(buffer, 0, n);

			total += n;

			if (total + maxread > getPayloadLength()) {
				maxread = getPayloadLength() - total;
			}

			if (total >= getPayloadLength()) {
				if (total != getPayloadLength()) {
					throw new IOException(
							"decodePackage payload invalid length");
				}

				break;
			}
		}

		sb.close();

		setPayload(sb.toByteArray());

		/* do CRC check inline ... */
		if (RUDPPacketCRC.CheckCRC(this) == false) {
			throw new IOException("CRC error");
		}
	}
}
