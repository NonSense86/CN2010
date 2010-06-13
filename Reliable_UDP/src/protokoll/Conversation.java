package protokoll;

import java.util.Vector;

public class Conversation {

	private int nextSeqNumber;
	private int awaitingSeqNumber;
	private Vector<Integer> sequenceNumbers;
	
	public Conversation() {
		nextSeqNumber = 1;
		awaitingSeqNumber = 1;
		sequenceNumbers = new Vector<Integer>();
	}
	
	public synchronized int getNextSeqNumber() {
		return nextSeqNumber++;
	}

	public int getAwaitingSeqNumber() {
		return awaitingSeqNumber;
	}

	public void setAwaitingSeqNumber(int awaitingSeqNumber) {
		this.awaitingSeqNumber = awaitingSeqNumber;
	}

	public Vector<Integer> getSequenceNumbers() {
		return sequenceNumbers;
	}
	
	
}
