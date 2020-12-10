package UDP;

import Application.ATM;
import Errors.ErrorCheck;
import Security.DiffieHelman;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client extends ATM {

    static public int port = 2020;
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

            InetAddress address = InetAddress.getByName("localhost");

            System.out.println("Please enter the number of the operation that you want to perform\n"
                    + "1: Login-Card Number\n" + "2: Balance\n" + "3: Withdraw\n" + "4: Deposit\n" + "5: Logout-Take Card\n");
            // Actions with ATM
            while (true) {

                demand = input.nextInt();

                ATM atm = new ATM();

                switch (demand) {
                    case 1: // Insert the card and login
                        System.out.println("Enter card number");
                        cardNum = input.nextInt();
                        System.out.println("Enter the pin");
                        pin = input.nextInt();
                        atm = new ATM (cardNum, pin);
                        break;

                    default:
                        System.out.println("Try again. No option");
                        break;
                }

                //client object sent to server

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(atm);

                byte[] outData = outputStream.toByteArray();
                byte[] encryptedOutData = diffieHelman.encryption(outData);

                ErrorCheck errorCheck = new ErrorCheck();
                errorCheck.getCRC32Checksum(encryptedOutData);
                //System.out.println(errorCheck.getCRC32Checksum(encryptedOutData));

                DatagramPacket outPacket = new DatagramPacket(encryptedOutData, encryptedOutData.length, address, port);

                byte[] inDataBuff = new byte[65507];

                DatagramPacket inPacket = new DatagramPacket(inDataBuff, inDataBuff.length);
                byte[] inData = inPacket.getData();

                //retransmission
                int attempts = 0;      // Packets may be lost, so we have to keep trying send data
                boolean ACK = false;

                do {
                    clientSocket.send(outPacket);
                    try {
                        clientSocket.receive(inPacket);

                        if (!inPacket.getAddress().equals(address)) {
                            throw new IOException("Unknown source");
                        } else {
                            System.out.println("Message received from: " + outPacket.getAddress().getHostAddress());
                        }
                        ACK = true;
                    } catch (InterruptedIOException e) {
                        attempts += 1;
                        System.out.println("Out of time. You have " + (MaxRetransmission - attempts) + " attempts");
                    }

                } while ((!ACK) && (attempts < MaxRetransmission));

                if (ACK) {
                    errorCheck.getCRC32Checksum(inData);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(inData);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    ATM response = (ATM)  objectInputStream.readObject();
                    switch (response.getDemand()) {
                        case 2:
                            if (demand == 1) {
                                System.out.println("You are logged in the system. Please select next option to perform"
                                        + "1: Login-Card Number\n" + "2: Balance\n" + "3: Withdraw\n" + "4: Deposit\n" + "5: Logout-Take Card\n");
                            }
                            break;

                        case 3:
                            if (demand == 1) {
                                System.out.println("Try again. Card Number or Pin are wrong");
                            } break;
                    }
                } else {
                    System.out.println("Without response -- giving up.");
                }
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