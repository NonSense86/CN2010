package protokoll;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RUDPPacket {

	private boolean IsSyn_;

	private boolean IsAck_;

	private boolean IsNull_;

	private boolean IsChecksum_;
	
	private boolean IsConnection_;

	private long checksum_;

	private int seqNumber_;

	private int ackNumber_;

	private int connectionId_;	

	private int payloadLength_;

	private byte[] payload_;

	/**
	 * SYN
	 */
	public boolean getIsSyn() {
		return IsSyn_;
	}

	public void setIsSyn(boolean IsSyn) {
		this.IsSyn_ = IsSyn;
	}

	/**
	 * ACK
	 */
	public boolean getIsAck() {
		return IsAck_;
	}

	public void setIsAck(boolean IsAck) {
		this.IsAck_ = IsAck;
	}

	/**
	 * NULL
	 */
	public boolean getIsNull() {
		return IsNull_;
	}

	public void setIsNull(boolean IsNull) {
		this.IsNull_ = IsNull;
	}

	/**
	 * CHECKSUM
	 */
	public boolean getIsChecksum() {
		return IsChecksum_;
	}

	public void setIsChecksum(boolean IsChecksum) {
		this.IsChecksum_ = IsChecksum;
	}
	
	/**
	 * CONNECTION
	 */
	public boolean getIsConnection() {
		return IsConnection_;
	}

	public void setIsConnection(boolean IsConnection) {
		this.IsConnection_ = IsConnection;
	}	

	/**
	 * checksum value
	 */
	public long getChecksum() {
		return checksum_;
	}

	private void setChecksum(long checksum) {
		this.checksum_ = checksum;
	}

	/**
	 * sequence number
	 */
	public int getSeqNumber() {
		return seqNumber_;
	}

	public void setSeqNumber(int seqNumber) {
		this.seqNumber_ = seqNumber;
	}

	/**
	 * acknowledge number
	 */
	public int getAckNumber() {
		return ackNumber_;
	}

	public void setAckNumber(int ackNumber) {
		this.ackNumber_ = ackNumber;
	}

	/**
	 * connection number
	 */
	public int getConnectionId() {
		return connectionId_;
	}

	public void setConnectionId(int connectionId) {
		this.connectionId_ = connectionId;
	}

	/**
	 * payload length
	 */
	public int getPayloadLength() {
		return payloadLength_;
	}

	private void setPayloadLength(int payloadLength) {
		this.payloadLength_ = payloadLength;
	}

	/**
	 * payload content
	 */
	public byte[] getPayload() {
		return payload_;
	}

	public void setPayload(byte[] payload) {
		this.payload_ = payload;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n");
		strBuilder.append("SYN=" + getIsSyn() + "\n");
		strBuilder.append("ACK=" + getIsAck() + "\n");
		strBuilder.append("NULL=" + getIsNull() + "\n");
		strBuilder.append("CHECKSUM=" + getIsChecksum() + "\n");
		strBuilder.append("CONN=" + getIsConnection() + "\n");
		strBuilder.append("\n");
		strBuilder.append("CHECKSUM=" + getChecksum() + "\n");
		strBuilder.append("SEQ=" + getSeqNumber() + "\n");
		strBuilder.append("ACK=" + getAckNumber() + "\n");
		strBuilder.append("CONNID=" + getConnectionId() + "\n");		
		strBuilder.append("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n");

		return strBuilder.toString();
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
		setChecksum(RUDPPacketCRC.GalculateCRCForPacket(this));

		/* write result to stream */
		ByteArrayOutputStream buffStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(buffStream);

		outStream.writeBoolean(getIsSyn());
		outStream.writeBoolean(getIsAck());
		outStream.writeBoolean(getIsNull());
		outStream.writeBoolean(getIsChecksum());
		outStream.writeBoolean(getIsConnection());

		outStream.writeLong(getChecksum());
		outStream.writeInt(getSeqNumber());
		outStream.writeInt(getAckNumber());
		outStream.writeInt(getConnectionId());		

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

		setIsSyn(inStream.readBoolean());
		setIsAck(inStream.readBoolean());
		setIsNull(inStream.readBoolean());
		setIsChecksum(inStream.readBoolean());
		setIsConnection(inStream.readBoolean());

		setChecksum(inStream.readLong());
		setSeqNumber(inStream.readInt());
		setAckNumber(inStream.readInt());
		setConnectionId(inStream.readInt());			

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
