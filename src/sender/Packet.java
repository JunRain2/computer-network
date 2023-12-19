package sender;

public class Packet {
	private int number;
	private int timer;
	private int ackCount;
	private boolean duplicatedAck;
	private boolean resendByDuplicatedAck;
	private boolean timeout;

	public Packet(int number) {
		this.number = number;
		reset();
	}

	public int getNumber() {
		return number;
	}

	public void ack() {
		this.ackCount++;
		if (ackCount%3 == 0) {
			duplicatedAck = true;
		}
	}

	public void accumulateAck() {
		this.ackCount = 1;
	}

	public void setResendByDuplicatedAck() {
		this.resendByDuplicatedAck = true;
	}

	public void reset() {
		this.timer = 0;
		this.ackCount = 0;
		this.duplicatedAck = false;
		this.resendByDuplicatedAck = false;
		this.timeout = false;
	}

	public boolean isResendByDuplicatedAck() {
		return resendByDuplicatedAck;
	}

	public void increaseTimer() {
		if (isAcked() || isDuplicatedAck()) {
			return;
		}
		if (timer == 4) {
			timeout = true;
			timer = 0;
			return;
		}

		timer++;
	}

	public boolean isDuplicatedAck() {
		return duplicatedAck;
	}

	public boolean isAcked() {
		return ackCount == 1;
	}

	public boolean isTimeout() {
		return timeout;
	}

	public void setDuplicatedAck() {
		this.duplicatedAck = false;
	}
}
