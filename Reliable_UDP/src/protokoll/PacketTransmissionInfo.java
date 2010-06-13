package protokoll;

import java.util.Date;

public class PacketTransmissionInfo {
	private int seqNumber_;	
	private Date transmissionDate_;
	
	public PacketTransmissionInfo(int seqNumber, Date transmissionDate) {
		this.seqNumber_ = seqNumber;
		this.transmissionDate_ = transmissionDate;
	}
	
	public PacketTransmissionInfo(PacketTransmissionInfo copy) {
		this.seqNumber_ = copy.seqNumber_;
		this.transmissionDate_ = new Date(copy.transmissionDate_.getTime());
	}
	
	public int getSeqNumber() {
		return seqNumber_;
	}
	
	public void setSeqNumber(int seqNumber) {
		seqNumber_ = seqNumber;
	}
	
	public Date getTransmissionDate() {
		return transmissionDate_;
	}
	
	public void setTransmissionDate(Date transmissionDate) {
		transmissionDate_ = transmissionDate;
	}	
}
