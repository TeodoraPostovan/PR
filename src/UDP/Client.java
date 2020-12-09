package UDP;

import Errors.ErrorCheck;
import Security.DiffieHelman;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {

    static public int port = 4300;
    private static final int TIMEOUT = 2000;   // Resend timeout (milliseconds)
    private static final int MaxRetransmission = 4;     // Maximum retransmissions
    private static String secretCode = "999";
    public static void main(String[] args) throws IOException {

        InetAddress address = InetAddress.getLocalHost();

        String input;
        Scanner data = new Scanner(System.in);
        input = data.nextLine();


        byte[] outBytes = input.getBytes();

        DatagramSocket clientSocket = new DatagramSocket();

        clientSocket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)

        // DatagramPacket outPacket = new DatagramPacket(outBytes, outBytes.length, address, port);

        String encryptedData = DiffieHelman.encryption(input, secretCode);

        DatagramPacket sendingPacket = new DatagramPacket(encryptedData.getBytes(), encryptedData.getBytes().length, address, port);

        DatagramPacket inPacket = new DatagramPacket(new byte[outBytes.length], outBytes.length);

        System.out.println(encryptedData);

        ErrorCheck checksum = new ErrorCheck();
        checksum.getCRC32Checksum(input.getBytes());

        int attempts = 0;      // Packets may be lost, so we have to keep trying
        boolean ACK = false;

        do {
            clientSocket.send(sendingPacket);          // Send the echo string
            try {
                clientSocket.receive(inPacket);  // Attempt echo reply reception

                if (!inPacket.getAddress().equals(address)) {// Check source
                    throw new IOException("Received packet from an unknown source");
                }
                ACK = true;
            } catch (InterruptedIOException e) {  // We did not get anything
                attempts += 1;
                System.out.println("Out of time. You have " + (MaxRetransmission - attempts) + " attempts");
            }

        } while ((!input.equals("close")) && (!ACK) && (attempts < MaxRetransmission));

        clientSocket.close();

        if (ACK) {
            System.out.println("Received: " + new String(inPacket.getData()));
        } else {
            System.out.println("No response -- giving up.");
        }

    }

}
