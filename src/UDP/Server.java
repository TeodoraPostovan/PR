package UDP;

import Application.ATM;
import Errors.ErrorCheck;
import Security.DiffieHelman;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {
    //    private static String SecretCode = "999";
    static  public int buffSize= 512;
    public static final int port = 2020;

    public static void main(String[] args) {
        DatagramSocket serverSocket;

        // byte[] receivedData = new byte[65535];

//        DatagramPacket inPacket = null;
//        while (true) {
//
//            inPacket = new DatagramPacket(receivedData, receivedData.length);
//
//            // receive the data in byte buffer.
//            serverSocket.receive(inPacket);
//
//            String finString = new String(receivedData, 0, inPacket.getLength());
//
//            String decryptedData = DiffieHelman.decryption(finString, SecretCode);
//            System.out.println("Client:-" + decryptedData);
//
//            // exit the server if the client sends "bye"
//            if (data(receivedData).toString().equals("bye")) {
//                System.out.println("Client sent bye.....EXITING");
//                break;
//            }
//
//            // clear the buffer after every message
//            receivedData = new byte[65535];
//        }
        try {
            serverSocket = new DatagramSocket(port);
            System.out.println("Wait a client to connect");
        } catch (SocketException se) {
            System.err.println("The socket can't be created");
            return;
        }

        DatagramPacket inPacket = new DatagramPacket(new byte[buffSize], buffSize); // DatagramPacket to receive data

        Map<InetAddress, Integer> session = new HashMap<>();
        DiffieHelman diffieHelman = new DiffieHelman();
        diffieHelman.setPublicKey(diffieHelman.getPKey());

        while (true) {
            try {
                inPacket.setLength(buffSize);
                serverSocket.receive(inPacket);
                byte[] inData = inPacket.getData();
                byte[] decryptedInData = diffieHelman.decryption(inData);
                ErrorCheck errorCheck = new ErrorCheck();
                errorCheck.getCRC32Checksum(decryptedInData);
                //System.out.println(errorCheck.getCRC32Checksum(inData));

                ByteArrayInputStream inputStream = new ByteArrayInputStream(decryptedInData);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                ATM atm = (ATM) objectInputStream.readObject();

                int request = atm.getDemand();
                Integer s = session.get(inPacket.getAddress());

                switch (request) {

                    case 1:
                        if (s != null) {
                            sendResponse(3,-1, inPacket.getAddress(), inPacket.getPort());
                            break;
                        } else {
                            int count = atm.consumers.size();
                            Boolean account = false;
                            for (int i= 0; i<=count; i++) {
                                if(ATM.consumers.get(i).getCardNumber() == atm.getCardNum()) {
                                    if (atm.consumers.get(i).getPin() == atm.getPin()) {
                                        session.put(inPacket.getAddress(), atm.getCardNum());
                                        sendResponse(2,-1,inPacket.getAddress(), inPacket.getPort());
                                        account = true;
                                        break;
                                    } else {
                                        sendResponse(3,-1, inPacket.getAddress(),inPacket.getPort());
                                        break;
                                    }
                                }
                            }
                            if(account == false) {
                                sendResponse(3, -1, inPacket.getAddress(), inPacket.getPort());
                                break;
                            }
                            break;
                        }
                    default:
                        sendResponse(3, -1, inPacket.getAddress(), inPacket.getPort());
                        break;

                } s = null;

                //System.out.println("C:" + decryptedInData);
            } catch (SocketTimeoutException ste) {    // receive() timed out
                System.err.println("Timed out!");
            } catch (Exception ioe) {
                System.err.println("Receive is bad");
                ioe.printStackTrace();
            }

        }
    }

    static public void sendResponse(int responseT, int responseC, InetAddress clientAddress, int clientPort) throws IOException {
        DatagramSocket datagramSocket;
        datagramSocket = new DatagramSocket();
        ATM response = new ATM (responseT, -1,-1,responseC);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(response);
        byte[] outData = outputStream.toByteArray();
        ErrorCheck errorCheck = new ErrorCheck();
        errorCheck.getCRC32Checksum(outData);
        DatagramPacket outPacket = new DatagramPacket(outData, outData.length, clientAddress, clientPort);
        datagramSocket.send(outPacket);

    }
}
