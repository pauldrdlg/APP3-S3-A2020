package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class DataLinkLayer extends Layer{
    @Override
    public void send(DatagramPacket packet, DatagramSocket socket) throws IOException {

        byte[] data = separateByteArrays(32, 199, packet.getData());

        //On ne trim pas les zéros de la data car on veut comparer le tout
        CRC32 crc = new CRC32();
        crc.update(data);

        System.out.println("CRC32: " + crc.getValue());

        byte[] crcByte = ByteBuffer.allocate(crcSize).putLong(crc.getValue()).array();

        // set CRC
        packet.setData(writeInoByteArrays(24, 31, packet.getData(), crcByte));

        socket.send(packet);

        if(next != null)
        {
            next.send(packet, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket) throws IOException {
        socket.receive(packet);

        CRC32 crc = new CRC32();
        crc.update(separateByteArrays(32, 199, packet.getData()));
        System.out.println("CRC32 DATA:"+ crc.getValue());

        if(previous != null)
        {
            previous.receive(packet, socket);
        }
    }
}
