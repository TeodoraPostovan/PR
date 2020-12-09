package UDP;

import Security.DiffieHelman;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    private static String secretCode = "999";
    public static void main(String[] args) throws IOException {

        DatagramSocket serverSocket = new DatagramSocket(4300);
        byte[] receivedData = new byte[65535];

        DatagramPacket inPacket = null;
        while (true) {

            inPacket = new DatagramPacket(receivedData, receivedData.length);

            // receive the data in byte buffer.
            serverSocket.receive(inPacket);

            String finString = new String(receivedData, 0, inPacket.getLength());

            String decryptedData = DiffieHelman.decryption(finString, secretCode);
            System.out.println("Client:-" + decryptedData);

            // exit the server if the client sends "bye"
            if (data(receivedData).toString().equals("bye"))
            {
                System.out.println("Client sent bye.....EXITING");
                break;
            }

            // clear the buffer after every message
            receivedData = new byte[65535];
        }
    }

    // method to convert the byte array data into a string representation
    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
