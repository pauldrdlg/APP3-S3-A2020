/*-------
        En-tête du paquet :
        - CRC : 4 bytes
        - Message : 20 bytes
        - Numéro : 4 bytes

        Contenu du paquet :
        - 172 bytes
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
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class TransportLayer extends Layer{
    private static final int dataSize = 172;
    private static final int messageSize = 20;
    private static final int CRCSize = 4;
    private static final int numberSize = 4;

    @Override
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName, byte[] buf) throws IOException{

        int nbPackets = 0;

        nbPackets =(int) (buf.length / dataSize) + 1;

        byte[] packet1 = createFirstPacket(fileName, nbPackets);

        packet.setData(packet1, 0, packet1.length);

        //System.out.println("Tansport send");

        if(next != null)
        {
            next.send(packet, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket) throws IOException {
        byte[] messageBytes = separateByteArrays(0, 19, packet.getData());
        messageBytes = trimZeros(messageBytes);
        String message = new String(messageBytes, 0, messageBytes.length);

        byte[] numberBytes = separateByteArrays(20, 23, packet.getData());
        int number = ByteBuffer.wrap(numberBytes).getInt();

        byte[] crcBytes = separateByteArrays(24, 27, packet.getData());
        int crc = ByteBuffer.wrap(crcBytes).getInt();

        byte[] dataBytes = separateByteArrays(28, 199, packet.getData());
        dataBytes = trimZeros(dataBytes);
        String data = new String(dataBytes, 0, dataBytes.length);

        System.out.println(message);
        System.out.println(number);
        System.out.println(crc);
        System.out.println(data);

        String fileName = "test.txt";


        //System.out.println("Transport receive");

        System.out.println(fileName.getBytes().length);

        System.out.println(fileName);

        //System.out.println(fileName.getBytes());
        //System.out.println("test.txt".getBytes());

        //fileName = "test.txt";

        byte[] completedFile = separateByteArrays(10, 255, packet.getData());

        if(previous != null)
        {
            previous.receive(packet, socket, fileName, trimZeros(completedFile));
        }
    }

    public byte[] fillWithZeros(int totalSize, byte[] buf) {
        byte[] temp = new byte[totalSize];
        for (int i = 0; i < totalSize - (totalSize - buf.length); i++) {
            temp[i] = buf[i];
        }

        return temp;

    }

    public byte[] addByteArrays(byte[] buf1, byte[] buf2) {
        int length = buf1.length + buf2.length;
        byte[] temp = new byte[length];

        int pos = 0;
        for (byte element : buf1) {
            temp[pos] = element;
            pos++;
        }

        for (byte element : buf2) {
            temp[pos] = element;
            pos++;
        }

        return temp;
    }

    public byte[] separateByteArrays(int startIndex, int EndIndex, byte[] buf) {
        byte[] temp = new byte[EndIndex - startIndex + 1];

        for (int i = startIndex; i <= EndIndex; i++) {
            temp[i - startIndex] = buf[i];
        }

        return temp;

    }

    public byte[] trimZeros(byte[] buf) {
        String str = new String(buf, 0, buf.length);

        int pos = str.indexOf(0);

        return (pos == -1 ? str : str.substring(0, pos)).getBytes();
    }

    public byte[] createFirstPacket(String fileName, int nbPackets) {
        byte[] message = "FIRST".getBytes();
        message = fillWithZeros(messageSize, message);

        byte[] number = new byte[numberSize];

        byte[] crc = new byte[CRCSize];

        byte[] data = (fileName + "**" + nbPackets).getBytes();
        data = fillWithZeros(dataSize, data);

        return addByteArrays(message,addByteArrays(number, addByteArrays(crc, data)));

    }
}
