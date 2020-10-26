package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import ServerUtility.*;

public class DataLinkLayer extends Layer{
    private int previousPacket = -1;
    private boolean simulation1 = true;
    private boolean simulation2 = true;

    @Override
    public void send(DatagramPacket packet, DatagramSocket socket) throws IOException {

        byte[] data = separateByteArrays(8, 199, packet.getData());

        //On ne trim pas les zéros de la data car on veut comparer le tout
        CRC32 crc = new CRC32();
        crc.update(data);

        //System.out.println("CRC32: " + crc.getValue());

        byte[] crcByte = ByteBuffer.allocate(crcSize).putLong(crc.getValue()).array();

        // Set CRC
        packet.setData(writeIntoByteArrays(0, 7, packet.getData(), crcByte));

        //Simulations erreurs
        byte[] numberBytes = separateByteArrays(28, 31, packet.getData());
        int number = ByteBuffer.wrap(numberBytes).getInt();

        if(number == 2 && simulation1)
        {
            simulation1 = false;
            packet = SimulationWrongCRC(packet);
        }

        /*if(number != 1)
        {
            simulation2 = false;*/
            socket.send(packet);
        //}

        if(next != null)
        {
            next.send(packet, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket, Log log) throws IOException {
        socket.receive(packet);

        CRC32 crc = new CRC32();
        crc.update(separateByteArrays(8, 199, packet.getData()));

        byte[] numberBytes = separateByteArrays(28, 31, packet.getData());
        int number = ByteBuffer.wrap(numberBytes).getInt();

        boolean verifyCRC = true;

        if(crc.getValue() != ByteBuffer.wrap(separateByteArrays(0, 7, packet.getData())).getLong())
        {
            log.addWarningToLog("CRC not corresponding on packet " + number + "!");
            log.addcrcPacketError();
            previousPacket++;
            verifyCRC = false;
        }
        else if(previousPacket + 1 != number)
        {
            log.addInfoToLog("Packet " + number + " received");

            for(int i = previousPacket + 1; i < number; i++)
            {
                log.addWarningToLog("Packet missing!");
                log.addLostPacket();
            }

            previousPacket = number;
        }
        else if(number == 0)
        {
            log.addInfoToLog("Packet 0: new file received");
            previousPacket++;
        }
        else
        {
            log.addInfoToLog("Packet " + number + " received");
            previousPacket++;
        }
        log.addReceivedPacket();

        if(previous != null && verifyCRC)
        {
            previous.receive(packet, socket, log);
        }
    }

    public DatagramPacket SimulationWrongCRC(DatagramPacket packet)
    {
        byte[] temp= packet.getData();

        temp = writeIntoByteArrays(70, 71, temp, new byte[2]);

        packet.setData(temp);

        return packet;
    }
}
