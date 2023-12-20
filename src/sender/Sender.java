package sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;

public class Sender {
	private static final String RECEIVER_HOST = "localhost";
	private static final int RECEIVER_PORT = 8002;
	private static final int SENDER_PORT = 8001;

	public static void main(String[] args) {
		CongWind congWind = new CongWind();
		Queue<Packet> timeoutList;
		int latestSendNumber = 99;
		int latestAckNumber = 99;
		int ack;

		try {
			while (true) {
				timeoutList = congWind.getTimeoutList();

				if (!timeoutList.isEmpty()) {
					Packet packet = timeoutList.poll();
					congWind.reset();

					if (packet.isResendByDuplicatedAck()) {
						congWind.fastRecovery();
						System.out.println("<<3-Dup ACK 사건 발생>>");
						System.out.println("Fast Recovery 실행--> cwind : " + congWind.getSize());
					} else if (packet.isTimeout()) {
						congWind.slowStart();
						System.out.println("<<타임아웃 사건 발생>>");
						System.out.println("Slow Start 실행 --> cwind : " + congWind.getSize());
					}
					latestSendNumber = latestAckNumber + congWind.getSize();

					congWind.addPacket(latestSendNumber);
					sendDate(latestSendNumber);
					System.out.println("임계값 : " + congWind.getCriticalPoint() + "로 설정");
					System.out.println("----------> 패킷 " + latestSendNumber + " 재전송");
				} else {
					latestSendNumber += congWind.getSize();
					congWind.addPacket(latestSendNumber);
					sendDate(latestSendNumber);
					System.out.println("----------> 패킷 " + latestSendNumber + " 송신");
				}

				ack = receiveDate();

				if (ack == -1) {
					congWind.increaseTimer();
				} else if (congWind.isAckedPacket(ack)) {
					congWind.timerReset();
					latestAckNumber = ack;
					if (congWind.getSize() > congWind.getCriticalPoint()) {
						congWind.linearIncrease();
						System.out.println(
							"<---ACK" + ack + " 수신 => cwin " + 1 + " 증가" + "(" + congWind.getSize()
								+ ")");
					} else if (congWind.getSize() < congWind.getCriticalPoint()) {
						congWind.exponentialIncrease();
						System.out.println(
							"<---ACK" + ack + " 수신 = >cwin " + Math.pow(2, congWind.getCount()-1) + " 증가" + "("
								+ congWind.getSize() + ")");
					}

				} else {
					System.out.println("<---ACK" + ack + " 수신");
				}

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static int receiveDate() {
		try (DatagramSocket socket = new DatagramSocket(SENDER_PORT)) {
			socket.setSoTimeout(1000);
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
			return Integer.parseInt(receivedMessage);
		} catch (IOException e) {
			return -1;
		}
	}

	private static void sendDate(int dataToSend) throws IOException {
		try (DatagramSocket socket = new DatagramSocket()) {
			byte[] sendData = Integer.toString(dataToSend).getBytes();
			InetAddress receiverAddress = InetAddress.getByName(RECEIVER_HOST);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receiverAddress, RECEIVER_PORT);
			socket.send(sendPacket);
		}
	}
}
