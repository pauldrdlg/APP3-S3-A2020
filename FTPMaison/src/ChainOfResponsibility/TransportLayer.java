/*-------
        En-tête du paquet :
        - Message : 20 bytes
        - Numéro : 4 bytes
        - CRC : 8 bytes

        Contenu du paquet :
        - 168 bytes
        - Premier paquet :
            - Nom du fichier
            - Numéro du dernier paquet
        - Contenu de fichier :
            - Data
        - Accusé :
            - DONE/FAIL

        Messages :
        - Premier paquet :
            FIRST
        - Contenu de fichier :
            DATA
        - Accusé de réception :
            ACK

        - Taille réelle du paquet --> toujours 200 bytes
         --------*/

package ChainOfResponsibility;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;

public class TransportLayer extends Layer{
    // Ces attributs servent à reconstituer le fichier complet à la réception
    private byte[] completedFile = new byte[0];
    private String fileName;
    private int nbPackets;

    // Cet attribut permet de savoir quels paquets ont été correctement reçus
    private static boolean[] received;

    // Ces attributs permettent de gérer les accusés de réception et de renvoyer des paquets au besoin
    private DatagramPacket[] listPackets;


    @Override
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName, byte[] buf) throws IOException{

        int nbPackets = (int) (buf.length / dataSize) + 1;

        byte[] packet1 = createFirstPacket(fileName, nbPackets);

        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        listPackets = new DatagramPacket[nbPackets + 1];

        listPackets[0] = new DatagramPacket(packet1, packet1.length, address, port);

        if (next != null) {
            next.send(listPackets[0], socket);
        }

        for(int i = 1; i <= nbPackets; i++)
        {
            int start = (i - 1) * dataSize;
            int end = i * dataSize - 1;

            if(end > buf.length - 1)
            {
                end = buf.length - 1;
            }

            System.out.println("from: " + start + ", to: " + end);

            byte[] packetX = createPacket(i, separateByteArrays(start, end, buf));

            listPackets[i] = new DatagramPacket(packetX, packetX.length, address, port);

            if(next != null)
            {
                next.send(listPackets[i], socket);
            }
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket) throws IOException {

        byte[] messageBytes = separateByteArrays(0, 19, packet.getData());
        messageBytes = trimZeros(messageBytes);
        String message = new String(messageBytes, 0, messageBytes.length);

        byte[] numberBytes = separateByteArrays(20, 23, packet.getData());
        int number = ByteBuffer.wrap(numberBytes).getInt();

        byte[] crcBytes = separateByteArrays(24, 31, packet.getData());
        long crc = ByteBuffer.wrap(crcBytes).getLong();

        byte[] dataBytes = separateByteArrays(32, 199, packet.getData());
        dataBytes = trimZeros(dataBytes);
        String data = new String(dataBytes, 0, dataBytes.length);

        switch (message)
        {
            case "FIRST":
                resetFile();

                fileName = data.split("@@")[0];
                nbPackets = Integer.parseInt(data.split("@@")[1]);

                received = new boolean[nbPackets + 1];
                received[0] = true;


                break;
            case "DATA":
                completedFile = addByteArrays(completedFile, dataBytes);
                received[number] = true;
                break;
            case "ACK":
                System.out.println("Accusé de réception du paquet " + number + " : " + data);
                break;
            default:
                break;
        }

        System.out.println(message);
        System.out.println(number);
        System.out.println(crc);
        System.out.println(data);

        if(previous != null)
        {
            previous.receive(packet, socket, fileName, completedFile);
        }
    }

    public byte[] createFirstPacket(String fileName, int nbPackets) {
        byte[] message = "FIRST".getBytes();
        message = fillWithZeros(messageSize, message);

        byte[] number = new byte[numberSize];

        byte[] crc = new byte[crcSize];

        byte[] data = (fileName + "@@" + nbPackets).getBytes();
        data = fillWithZeros(dataSize, data);

        return addByteArrays(message,addByteArrays(number, addByteArrays(crc, data)));
    }

    public byte[] createPacket(int numberPacket, byte[] dataPacket)
    {
        byte[] message = "DATA".getBytes();
        message = fillWithZeros(messageSize, message);

        byte[] number = ByteBuffer.allocate(numberSize).putInt(numberPacket).array();

        byte[] crc = new byte[crcSize];

        byte[] data = dataPacket;
        data = fillWithZeros(dataSize, data);

        return addByteArrays(message,addByteArrays(number, addByteArrays(crc, data)));
    }

    public byte[] createACKPacket(int numberPacket, boolean received) {
        byte[] message = "ACK".getBytes();
        message = fillWithZeros(messageSize, message);

        byte[] number = ByteBuffer.allocate(numberSize).putInt(numberPacket).array();

        byte[] crc = new byte[crcSize];

        byte[] data = new byte[0];

        if (received) {
            data = "DONE".getBytes();
        } else {
            data = "FAIL".getBytes();
        }
        data = fillWithZeros(dataSize, data);

        return addByteArrays(message,addByteArrays(number, addByteArrays(crc, data)));
    }

    public void sendACKPacket(int numberPacket, boolean received) {
        /*code pour envoyer un paquet ACK*/
    }

    public void resetFile() {
        completedFile = new byte[0];
    }
}
