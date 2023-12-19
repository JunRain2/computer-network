package receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {
	private static final String SENDER_HOST = "localhost";
	private static final Integer SENDER_PORT = 8001;
	private static final Integer RECEIVER_PORT = 8002;

	public static void main(String[] args) {
		Ack ack = new Ack();
		while (ack.getNumber() != 115) {
			try {
				// 데이터를 수신하기
				int receivedMessage = receiveDate();
				System.out.println("----------> 패킷 " + receivedMessage + " 수신");

				if (receivedMessage == ack.getNumber() + 1) {
					ack.increaseNumber();
				}
				sendData(ack.getNumber());

				System.out.println("<--- ACK" + ack.getNumber() + "송신");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void sendData(int data) throws IOException {
		String dataInteger = Integer.toString(data);

		// DatagramSocket 생성
		DatagramSocket socket = new DatagramSocket();

		// 보낼 데이터를 바이트 배열로 변환
		byte[] sendData = dataInteger.getBytes();

		// 상대방의 주소 설정
		InetAddress senderAddress = InetAddress.getByName(SENDER_HOST);

		// DatagramPacket 생성
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, senderAddress, SENDER_PORT);

		// 데이터 전송
		socket.send(sendPacket);

		// 소켓 닫기
		socket.close();
	}

	private static int receiveDate() throws IOException {
		int receivedMessageInt;

		// DatagramSocket 생성
		DatagramSocket socket = new DatagramSocket(RECEIVER_PORT);

		// 수신용 바이트 배열 생성
		byte[] receiveData = new byte[1024];

		// DatagramPacket 생성
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		// 데이터 수신
		socket.receive(receivePacket);

		// 수신한 데이터 출력
		String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

		receivedMessageInt = Integer.parseInt(receivedMessage);

		// 소켓 닫기
		socket.close();

		return receivedMessageInt;
	}
}
