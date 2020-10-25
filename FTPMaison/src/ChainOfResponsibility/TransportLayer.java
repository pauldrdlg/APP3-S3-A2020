/*-------
        En-tête du paquet :
        - CRC : 8 bytes
        - Message : 20 bytes
        - Numéro : 4 bytes

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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import ServerUtility.*;

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

            //System.out.println("from: " + start + ", to: " + end);

            byte[] packetX = createPacket(i, separateByteArrays(start, end, buf));

            listPackets[i] = new DatagramPacket(packetX, packetX.length, address, port);
        }

        if(next != null)
        {
            next.send(listPackets, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket, Log log, FileToSave fileToSave) throws IOException {
        byte[] crcBytes = separateByteArrays(0, 7, packet.getData());
        long crc = ByteBuffer.wrap(crcBytes).getLong();

        byte[] messageBytes = separateByteArrays(8, 27, packet.getData());
        messageBytes = trimZeros(messageBytes);
        String message = new String(messageBytes, 0, messageBytes.length);

        byte[] numberBytes = separateByteArrays(28, 31, packet.getData());
        int number = ByteBuffer.wrap(numberBytes).getInt();

        byte[] dataBytes = separateByteArrays(32, 199, packet.getData());
        dataBytes = trimZeros(dataBytes);
        String data = new String(dataBytes, 0, dataBytes.length);

        if(number == 0)
        {
            String[] result = data.split("@@");

            fileToSave.setFilename(result[0]);
            fileToSave.setNbPacket(Integer.parseInt(result[1]));
        }
        else
        {
            fileToSave.addData(data);
        }

        /*System.out.println(crc);
        System.out.println(message);
        System.out.println(number);
        System.out.println(data);*/

        if(previous != null)
        {
            previous.receive(packet, socket, log, fileToSave);
        }
    }

    public byte[] createFirstPacket(String fileName, int nbPackets) {
        byte[] crc = new byte[crcSize];

        byte[] message = "FIRST".getBytes();
        message = fillWithZeros(messageSize, message);

        byte[] number = new byte[numberSize];

        byte[] data = (fileName + "@@" + nbPackets).getBytes();
        data = fillWithZeros(dataSize, data);

        return addByteArrays(crc,addByteArrays(message, addByteArrays(number, data)));
    }

    public byte[] createPacket(int numberPacket, byte[] dataPacket)
    {
        byte[] crc = new byte[crcSize];

        byte[] message = "DATA".getBytes();
        message = fillWithZeros(messageSize, message);

        byte[] number = ByteBuffer.allocate(numberSize).putInt(numberPacket).array();

        byte[] data = dataPacket;
        data = fillWithZeros(dataSize, data);

        return addByteArrays(crc,addByteArrays(message, addByteArrays(number, data)));
    }
}
