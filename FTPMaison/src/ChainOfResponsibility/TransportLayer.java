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

import ServerUtility.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class TransportLayer extends Layer{
    // Ces attributs servent à reconstituer le fichier complet à la réception
    private byte[] completedFile = new byte[0];
    private String fileName;
    private int nbPackets;

    // Cet attribut permet de savoir quels paquets ont été correctement reçus
    private static boolean[] received = new boolean[0];
    private int previousPacket = 0;
    private boolean allReceived = true;
    private int errorCount = 0;

    // Ces attributs permettent de gérer les accusés de réception et de renvoyer des paquets au besoin
    private DatagramPacket[] listPackets;

    @Override
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName, byte[] buf, String error) throws IOException{
        int nbPackets = (buf.length / dataSize) + 1;

        byte[] packet1 = createFirstPacket(fileName, nbPackets);

        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        listPackets = new DatagramPacket[nbPackets + 1];

        listPackets[0] = new DatagramPacket(packet1, packet1.length, address, port);
        DatagramPacket temp0 = new DatagramPacket(packet1, packet1.length, address, port);

        if (next != null) {
            next.send(temp0, socket, error);
        }

        for(int i = 1; i <= nbPackets; i++)
        {
            int start = (i - 1) * dataSize;
            int end = i * dataSize - 1;

            if(end > buf.length - 1)
            {
                end = buf.length - 1;
            }

            byte[] packetToSave = createPacket(i, separateByteArrays(start, end, buf));
            byte[] packetToSend = createPacket(i, separateByteArrays(start, end, buf));

            listPackets[i] = new DatagramPacket(packetToSave, packetToSave.length, address, port);

            if(next != null)
            {
                DatagramPacket temp = new DatagramPacket(packetToSend, packetToSend.length, address, port);

                next.send(temp, socket, error);
            }
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket, Log log) throws IOException, TransmissionErrorException{
        byte[] messageBytes = separateByteArrays(8, 27, packet.getData());
        messageBytes = trimZeros(messageBytes);
        String message = new String(messageBytes, 0, messageBytes.length);

        byte[] numberBytes = separateByteArrays(28, 31, packet.getData());
        int number = ByteBuffer.wrap(numberBytes).getInt();

        byte[] dataBytes = separateByteArrays(32, 199, packet.getData());
        byte[] dataBytesNotTrimmed = dataBytes;
        dataBytes = trimZeros(dataBytes);
        String data = new String(dataBytes, 0, dataBytes.length);

        byte[] ACKpacketContent;

        switch (message)
        {
            case "FIRST":
                resetFile();

                fileName = data.split("@@")[0];
                nbPackets = Integer.parseInt(data.split("@@")[1]);
                received = new boolean[nbPackets + 1];
                received[number] = true;

                completedFile = new byte[nbPackets * 168];

                ACKpacketContent = createACKPacket(number, true);

                sendACKPacket(ACKpacketContent, packet, socket);
                break;

            case "DATA":
                received[number] = true;

                for (int i = previousPacket + 1; i < number; i++) {
                    errorCount++;

                    if(errorCount == 3)
                    {
                        throw new TransmissionErrorException("3 errors found");
                    }

                    ACKpacketContent = createACKPacket(i, false);
                    sendACKPacket(ACKpacketContent, packet, socket);
                }

                ACKpacketContent = createACKPacket(number, true);
                sendACKPacket(ACKpacketContent, packet, socket);

                completedFile = writeIntoByteArrays(168 * (number - 1), (168 * number) - 1, completedFile, dataBytesNotTrimmed);

                previousPacket = number;
                break;

            case "ACK":
                System.out.println("Accusé de réception du paquet " + number + " : " + data);

                if (data.equals("FAIL")) {
                    System.out.println("Renvoi du paquet " + number);
                    next.send(listPackets[number], socket, "0");
                }
                break;
            default: break;
        }

        allReceived = true;

        for (int i = 0; i < received.length; i++) {
            if (!received[i]) {
                allReceived = false;
            }
        }

        if(received.length != 0 && previous != null && allReceived)
        {
            System.out.println("Tous les paquets ont été reçus!");
            completedFile = trimZeros(completedFile);
            previous.receive(packet, socket, log, fileName, completedFile);
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

    public byte[] createPacket(int numberPacket, byte[] dataPacket) {
        byte[] crc = new byte[crcSize];

        byte[] message = "DATA".getBytes();
        message = fillWithZeros(messageSize, message);

        byte[] number = ByteBuffer.allocate(numberSize).putInt(numberPacket).array();

        byte[] data = dataPacket;
        data = fillWithZeros(dataSize, data);

        return addByteArrays(crc,addByteArrays(message, addByteArrays(number, data)));
    }

    public byte[] createACKPacket(int numberPacket, boolean received) {
        byte[] crc = new byte[crcSize];

        byte[] message = "ACK".getBytes();
        message = fillWithZeros(messageSize, message);

        byte[] number = ByteBuffer.allocate(numberSize).putInt(numberPacket).array();

        byte[] data;

        if (received) {
            data = "DONE".getBytes();
        } else {
            data = "FAIL".getBytes();
        }
        data = fillWithZeros(dataSize, data);

        return addByteArrays(crc,addByteArrays(message, addByteArrays(number, data)));
    }

    public void sendACKPacket(byte[] packetContent, DatagramPacket packet, DatagramSocket socket) throws IOException {
        packet.setData(packetContent, 0, packetContent.length);

        if(next != null)
        {
            next.send(packet, socket, "0");
        }
    }

    public void resetFile() {
        completedFile = new byte[0];
        received = new boolean[0];
        previousPacket = 0;
        errorCount = 0;
    }
}
