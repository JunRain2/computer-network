package sender;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CongWind {
	private List<Packet> packetList = new ArrayList<>();
	private Queue<Packet> timeoutList = new LinkedList<>();
	private int size;
	private int criticalPoint;
	private int count = 1;

	public CongWind() {
		this.size = 9;
		this.criticalPoint = 0;
	}

	public int getCriticalPoint() {
		return criticalPoint;
	}

	public void linearIncrease() {
		size++;
	}

	public void exponentialIncrease() {
		size += Math.pow(2, count++);
	}

	public int getCount() {
		return count;
	}

	// timeOut 발생시
	public void slowStart() {
		criticalPoint = (int)Math.ceil((double)size/2);
		size = 1;
		count = 1;
	}

	// 3Dup 발생시
	public void fastRecovery() {
		criticalPoint = (int)Math.ceil((double)size/2);
		size = criticalPoint + 3;
		count = 1;
	}

	// Packet을 추가
	public Packet addPacket(int number) {
		Packet packet = new Packet(number);
		packetList.add(packet);
		return packet;
	}

	public boolean isAckedPacket(int number) {
		Packet packet = packetList.stream()
			.filter((p) -> number == p.getNumber())
			.findFirst().orElse(addPacket(number));

		accumulateAck(number);
		packet.ack();

		if (packet.isDuplicatedAck()) {
			packet.setDuplicatedAck();
			Packet resendPacket = packetList.stream()
				.filter((p) -> number < p.getNumber())
				.findFirst().get();

			resendPacket.setResendByDuplicatedAck();
			timeoutList.add(resendPacket);
		}

		return packet.isAcked();
	}

	public void accumulateAck(int number) {
		packetList.stream()
			.filter((p) -> number > p.getNumber())
			.forEach(Packet::accumulateAck);
	}

	public void increaseTimer() {
		for (Packet packet : packetList) {
			if (packet.isTimeout()) {
				timeoutList.add(packet);
				return;
			}

			packet.increaseTimer();
		}
	}

	public int getSize() {
		return size;
	}

	public Queue<Packet> getTimeoutList() {
		return timeoutList;
	}

	public void timerReset() {
		packetList.stream()
			.forEach(Packet::timerReset);
	}

	public void reset() {
		timerReset();
		packetList = new ArrayList<>();
	}
}
