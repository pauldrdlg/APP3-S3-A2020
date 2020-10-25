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
            - Vide

        Messages :
        - Premier paquet :
            FIRST
        - Contenu de fichier :
            DATA
        - Accusé :
            ACCUSE-DONE/FAIL

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

    @Override
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName, byte[] buf) throws IOException{

        int nbPackets = (int) (buf.length / dataSize) + 1;

        byte[] packet1 = createFirstPacket(fileName, nbPackets);

        DatagramPacket[] listPackets = new DatagramPacket[nbPackets + 1];

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        listPackets[0] = new DatagramPacket(packet1, packet1.length, address, port);

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
        }

        if(next != null)
        {
            next.send(listPackets, socket);
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

        System.out.println(message);
        System.out.println(number);
        System.out.println(crc);
        System.out.println(data);

        String fileName = "test.txt";

        byte[] completedFile = separateByteArrays(10, 255, packet.getData());

        if(previous != null)
        {
            previous.receive(packet, socket, fileName, trimZeros(completedFile));
        }
    }

    public byte[] createFirstPacket(String fileName, int nbPackets) {
        byte[] message = "FIRST".getBytes();
        message = fillWithZeros(messageSize, message);

        byte[] number = new byte[numberSize];

        byte[] crc = new byte[crcSize];

        byte[] data = (fileName + "**" + nbPackets).getBytes();
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
}
