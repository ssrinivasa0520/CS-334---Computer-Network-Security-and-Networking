import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class robot {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		int listenPort = 3310;
				
		System.out.println("Robot started");
		
		System.out.println();
		
		//Create a TCP socket to accept connection request
		System.out.print("Creating TCP socket for listening and accepting connection...");
		ServerSocket listenSocket = new ServerSocket(listenPort);
		System.out.println("Done");
		
		System.out.println("\nReady to accept connection on port "+listenPort);
		System.out.println("Waiting for connection...");
		
		//When accept is called, the function is blocked and wait until a client is connected
		Socket s1 = listenSocket.accept();
		//The listen socket is no longer required
		listenSocket.close();

		// ---------------------------------------------------------- //
		// 							Step 2 							  //
		// ---------------------------------------------------------- //		
		BufferedReader s1_in = new BufferedReader(new InputStreamReader(s1.getInputStream()));
		char[] studentID = new char[10];
		int byteRead = 0;
		int byteLeft = 10;
		do{
			byteRead = s1_in.read(studentID, byteRead, byteLeft);
		}while ((byteLeft -= byteRead) > 0); 
		System.out.println("Student ID received: "+ new String(studentID));
		
		//Get the information about the connection accepted by getting information from the socket s1
		System.out.println("\nClient from "+s1.getInetAddress().getHostAddress()+" at port "+s1.getPort()+" connected");
		String studentIP = s1.getInetAddress().getHostAddress();

		// ---------------------------------------------------------- //
		// 							Step 3 							  //
		// ---------------------------------------------------------- //		
		//Generate a random port number and ask STUDENT to listen
		Random random = new Random();
		int iTCPPort2Connect = random.nextInt()%10000 + 20000; 
		iTCPPort2Connect = 10000;

		System.out.print("Requesting STUDENT to accept TCP <"+iTCPPort2Connect+">...");
		//Send the port required to the STUDENT
		PrintWriter s1_out = null;
		s1_out = new PrintWriter(s1.getOutputStream());
		s1_out.write(iTCPPort2Connect +"");
		s1_out.flush();
		System.out.println("Done");
		
		Thread.sleep(1000);
		System.out.print("\nConnecting to the STUDENT s1 <"+iTCPPort2Connect+">...");
		
		//Connect to the server (student s2)
		Socket s2 = new Socket(studentIP, iTCPPort2Connect);
		System.out.println("Done");

		// ---------------------------------------------------------- //
		// 							Step 4 							  //
		// ---------------------------------------------------------- //		
		//Send the ports required to STUDENT
		int iUDPPortRobot = random.nextInt()%10000 + 20000;
		int iUDPPortStudent = random.nextInt()%10000 + 20000; 
		System.out.print("Sending the UDP information: ROBOT: <"+iUDPPortRobot+">, STUDENT: <"+iUDPPortStudent+">...");
		
		PrintWriter s2_out = new PrintWriter(s2.getOutputStream());
		s2_out.write(iUDPPortRobot+","+iUDPPortStudent+".");
		s2_out.flush();
		System.out.println("Done");
		
		
		//Create a UDP socket to send the data
		System.out.print("\nPreparing socket to send to s3 <"+iUDPPortStudent+">...");
		DatagramSocket s3 = new DatagramSocket(iUDPPortRobot);
		System.out.println("Done");
		
		byte[] buff = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(buff, 0,  buff.length);
		s3.receive(receivePacket);
		String num = new String(receivePacket.getData(), 0, receivePacket.getLength());
		byte[] numBytes = num.getBytes();
		int n = (int)numBytes[0]-48;
		System.out.println("Get num = " + num + " n = "+n);

		System.out.println("Sending UDP packets:");
		String messageToTransmit = "";
		for (int i=0; i<n; i++)
			messageToTransmit += String.format("%05d", Math.abs(random.nextInt()%10000));
		System.out.println("Message to transmit: "+messageToTransmit);
		DatagramPacket sendPacket = new DatagramPacket(messageToTransmit.getBytes(), 0,  messageToTransmit.length(), InetAddress.getByName(studentIP), iUDPPortStudent);
		
		for (int i=0; i<5; i++){
			s3.send(sendPacket);
			Thread.sleep(1000);
			System.out.println("UDP packet "+(i+1)+" sent");
		}
		
		// ---------------------------------------------------------- //
		// 							Step 3 							  //
		// ---------------------------------------------------------- //
		System.out.println("\nReceiving UDP packet:");
		s3.receive(receivePacket);
		String receiveStr = new String(receivePacket.getData(), 0, receivePacket.getLength());
		System.out.println("Received: "+receiveStr);
		while(receiveStr.compareTo(Integer.toString(n)) == 0) {
			s3.receive(receivePacket);
			receiveStr = new String(receivePacket.getData(), 0, receivePacket.getLength());
			System.out.println("Received: "+receiveStr);		
		}
		
		if (receiveStr.compareTo(messageToTransmit) == 0)
			System.out.println("\nThe two strings are the same.");
		else
			System.out.println("\nThe two strings are not the same.");
		
		s1_in.close();
		s1_out.close();
		s2_out.close();
		s1.close();
		s2.close();
		s3.close();
	}

}
