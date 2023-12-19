package sender;

public class Packet {
	private int number;
	private int congWind;
	private int timer;

	public Packet() {
		number = 100;
		congWind = 1;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public int getNumber() {
		return number;
	}

	public void increaseNumber() {
		this.number++;
	}

	public int getCongWind() {
		return congWind;
	}

	public void increaseCongWind() {
		this.congWind++;
	}
}
