package receiver;

public class Ack {
	private int number;
	private int wind;
	private int timer;

	public Ack() {
		this.number = 99;
		this.wind = 1;
		this.timer = 0;
	}

	public int getNumber() {
		return number;
	}

	public void increaseNumber() {
		this.number++;
	}

	public int getWind() {
		return wind;
	}

	public void setWind(int wind) {
		this.wind = wind;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}
}
