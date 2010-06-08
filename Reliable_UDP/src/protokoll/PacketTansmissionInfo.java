package protokoll;

import java.util.Date;

public class PacketTansmissionInfo {
	private int seqNumber_;	
	private Date transmissionDate_;
	
	public PacketTansmissionInfo(int seqNumber, Date transmissionDate) {
		this.seqNumber_ = seqNumber;
		this.transmissionDate_ = transmissionDate;
	}
	
	public PacketTansmissionInfo(PacketTansmissionInfo copy) {
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
