package UDP;

import Application.ATM;
import Errors.ErrorCheck;
import Security.DiffieHelman;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Map;

public class Server {
    //    private static String SecretCode = "999";
    static  public int buffSize= 256;
    public static final int port = 4300;

    public static void main(String[] args) throws IOException {
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
        try{
            serverSocket = new DatagramSocket(4300);
            System.out.println("Wait a client to connect");
        } catch ( SocketException se) {
            System.err.println("The socket can't be created");
            return;
        }

        DatagramPacket inPacket = new DatagramPacket(new byte[buffSize], buffSize);
        DiffieHelman diffieHelman = new DiffieHelman();
        diffieHelman.setPublicKey(diffieHelman.getPKey());

        while(true) {
            try {
                inPacket.setLength(buffSize);
                serverSocket.receive(inPacket);
                byte[] inData = inPacket.getData();
                byte[] decryptedInData = diffieHelman.decryption(inData);
                ErrorCheck errorCheck = new ErrorCheck();
                errorCheck.getCRC32Checksum(decryptedInData);

                ByteArrayInputStream inputStream = new ByteArrayInputStream(decryptedInData);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                ATM atm = (ATM) objectInputStream.readObject();

                System.out.println("C:" + decryptedInData);
            } catch (SocketTimeoutException ste) {    // receive() timed out
                System.err.println("Timed out!");
            } catch (Exception ioe) {
                System.err.println("Receive is bad");
                ioe.printStackTrace();
            }

        }
    }
}
