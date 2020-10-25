package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import ServerUtility.*;

public class DataLinkLayer extends Layer{
    @Override
    public void send(DatagramPacket[] listPackets, DatagramSocket socket) throws IOException {

        for(int i = 0; i < listPackets.length; i++)
        {
            byte[] data = separateByteArrays(8, 199, listPackets[i].getData());

            //On ne trim pas les zéros de la data car on veut comparer le tout
            CRC32 crc = new CRC32();
            crc.update(data);

            //System.out.println("CRC32: " + crc.getValue());

            byte[] crcByte = ByteBuffer.allocate(crcSize).putLong(crc.getValue()).array();

            // set CRC
            listPackets[i].setData(writeIntoByteArrays(0, 7, listPackets[i].getData(), crcByte));

            socket.send(listPackets[i]);
        }

        if(next != null)
        {
            next.send(listPackets, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket, Log log, FileToSave fileToSave) throws IOException {
        socket.receive(packet);

        CRC32 crc = new CRC32();
        crc.update(separateByteArrays(8, 199, packet.getData()));
        System.out.println("CRC32 DATA: "+ crc.getValue());

        log.addReceivedPacket();

        if(crc.getValue() != ByteBuffer.wrap(separateByteArrays(0, 7, packet.getData())).getLong())
        {
            log.addcrcPacketError();
            fileToSave.addError();
        }

        if(previous != null)
        {
            previous.receive(packet, socket, log, fileToSave);
        }
    }
}
