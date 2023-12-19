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
		this.size = 1;
		this.criticalPoint = 0;
	}

	public int getCriticalPoint() {
		return criticalPoint;
	}

	public void linearIncrease(int tmp) {
		size += tmp;
	}

	public void exponentialIncrease(int tmp) {
		size += Math.pow(2, count++);
	}

	// timeOut 발생시
	public void slowStart() {
		criticalPoint = size / 2;
		size = 1;
		count = 1;
	}

	// 3Dup 발생시
	public void fastRecovery() {
		criticalPoint = size / 2;
		size = criticalPoint + 3;
	}

	// 전송할 때 Packet을 추가
	public void addPacket(int number) {
		Packet packet = new Packet(number);
		packetList.add(packet);
	}

	public boolean isAckedPacket(int number) {
		Packet packet = packetList.stream()
			.filter((p) -> number == p.getNumber())
			.findFirst().get();

		accumulateAck(number);
		packet.ack();

		if (packet.isDuplicatedAck()) {
			packet.setDuplicatedAck();
			Packet resendPacket = packetList.stream()
				.filter((p) -> number + 1 == p.getNumber())
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
}
