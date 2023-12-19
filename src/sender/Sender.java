package sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender {
	private static final String RECEIVER_HOST = "localhost";
	private static final Integer RECEIVER_PORT = 8002;
	private static final Integer SENDER_PORT = 8001;

	public static void main(String[] args) {
		Packet packet = new Packet();
		// ack 를 통해 응답된 패킷 번호
		int ackedNum = 100;
		// 전송된 ack
		int receiveAck;

		while (ackedNum != 115) {
			try {
				sendDate(packet.getNumber());
				receiveAck = receiveDate();

				if (receiveAck == ackedNum) {
					// ack 잘 옴 -> wind 증가
					packet.increaseCongWind();
					System.out.println(
						"<--- ACK" + receiveAck + " 수신 =>" + "cwin 1 증가(" + packet.getCongWind() + ")");
					ackedNum++;
				} else {
					// ack 잘 못 옴 -> wind 증가 안함
					System.out.println("<--- ACK" + receiveAck + " 수신");
				}

				packet.increaseNumber();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static int receiveDate() throws IOException {
		// DatagramSocket 생성
		DatagramSocket socket = new DatagramSocket(SENDER_PORT);

		// 수신용 바이트 배열 생성
		byte[] receiveData = new byte[1024];

		// DatagramPacket 생성
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		// 데이터 수신
		socket.receive(receivePacket);

		// 수신한 데이터
		String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

		// 소켓 닫기
		socket.close();

		return Integer.parseInt(receivedMessage);
	}

	private static void sendDate(int dataToSend) throws IOException {
		// DatagramSocket 생성
		DatagramSocket socket = new DatagramSocket();

		// 보낼 데이터를 바이트 배열로 변환
		byte[] sendData = Integer.toString(dataToSend).getBytes();

		// 상대방의 주소 설정
		InetAddress receiverAddress = InetAddress.getByName(RECEIVER_HOST);

		// DatagramPacket 생성
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receiverAddress, RECEIVER_PORT);

		// 데이터 전송
		socket.send(sendPacket);

		System.out.println("----------> 패킷 " + dataToSend + "송신");

		// 소켓 닫기
		socket.close();
	}
}
