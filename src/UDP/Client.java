package UDP;

import Application.ATM;
import Errors.ErrorCheck;
import Security.DiffieHelman;

import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client extends ATM {

    static public int port = 4300;
    private static final int TIMEOUT = 2000;   // Resend timeout
    private static final int MaxRetransmission = 4;     // Maximum retransmissions
    // private static String SecretCode = "999";

    static DatagramSocket clientSocket;

    public void initiateClient() {
        try {
            Scanner input = new Scanner(System.in);
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(TIMEOUT);
            DiffieHelman diffieHelman = new DiffieHelman();
            diffieHelman.setPublicKey(diffieHelman.getPKey());

            InetAddress address = InetAddress.getLocalHost();

            System.out.println("Please enter the number of the operation that you want to perform\n"
                    + "1: Login-Card Number\n" + "2: Balance\n" + "3: Withdraw\n" + "4: Deposit\n" + "5: Logout-Take Card\n");
            ATM atm = new ATM();

            // Actions with ATM

            //client object sent to server


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(atm);

            byte[] outData = outputStream.toByteArray();
            byte[] encryptedOutData = diffieHelman.encryption(outData);

            ErrorCheck errorCheck = new ErrorCheck();
            errorCheck.getCRC32Checksum(encryptedOutData);

            DatagramPacket outPacket = new DatagramPacket(encryptedOutData, encryptedOutData.length, address, port);

            byte[] inDataBuff = new byte[65535];

            DatagramPacket inPacket = new DatagramPacket(inDataBuff, inDataBuff.length);

            byte[] inData = inPacket.getData();

            int attempts = 0;      // Packets may be lost, so we have to keep trying
            boolean ACK = false;

            do {
                clientSocket.send(outPacket);
                try {
                    clientSocket.receive(inPacket);

                    if (!inPacket.getAddress().equals(address)) {
                        throw new IOException("Received packet from an unknown source");
                    }
                    ACK = true;
                } catch (InterruptedIOException e) {
                    attempts += 1;
                    System.out.println("Out of time. You have " + (MaxRetransmission - attempts) + " attempts");
                }

            } while ((!input.equals("close")) && (!ACK) && (attempts < MaxRetransmission));

            if (ACK) {
                System.out.println("Received: " + new String(inPacket.getData()));
            } else {
                System.out.println("Without response -- giving up.");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        clientSocket.close();

//    public static void main(String[] args) throws IOException {
//
//        InetAddress address = InetAddress.getLocalHost();
//
//        String input;
//        Scanner data = new Scanner(System.in);
//        input = data.nextLine();
//
//
//        byte[] outBytes = input.getBytes();
//
//        DatagramSocket clientSocket = new DatagramSocket();
//
//        clientSocket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)
//
//       // DatagramPacket outPacket = new DatagramPacket(outBytes, outBytes.length, address, port);
//
//        String encryptedData = DiffieHelman.encryption(input, secretCode);
//
//        DatagramPacket sendingPacket = new DatagramPacket(encryptedData.getBytes(), encryptedData.getBytes().length, address, port);
//
//        DatagramPacket inPacket = new DatagramPacket(new byte[outBytes.length], outBytes.length);
//
//        System.out.println(encryptedData);
//
//        ErrorCheck checksum = new ErrorCheck();
//        checksum.getCRC32Checksum(input.getBytes());
//
//        int attempts = 0;      // Packets may be lost, so we have to keep trying
//        boolean ACK = false;
//
//        do {
//            clientSocket.send(sendingPacket);
//            try {
//                clientSocket.receive(inPacket);
//
//                if (!inPacket.getAddress().equals(address)) {
//                    throw new IOException("Received packet from an unknown source");
//                }
//                ACK = true;
//            } catch (InterruptedIOException e) {
//                attempts += 1;
//                System.out.println("Out of time. You have " + (MaxRetransmission - attempts) + " attempts");
//            }
//
//        } while ((!input.equals("close")) && (!ACK) && (attempts < MaxRetransmission));
//
//        clientSocket.close();
//
//        if (ACK) {
//            System.out.println("Received: " + new String(inPacket.getData()));
//        } else {
//            System.out.println("Without response -- giving up.");
//        }
//
//    }
    }

    public static void main(String[] args) {
        Client consumer = new Client();
        consumer.initiateClient();
    }
}