package protokoll;

public class Barrier {
	public synchronized void block() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void release() {
		notify();
	}

	public synchronized void releaseAll() {
		notifyAll();
	}
}
