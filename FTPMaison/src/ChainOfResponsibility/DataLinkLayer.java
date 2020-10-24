package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.zip.CRC32;

public class DataLinkLayer extends Layer{
    @Override
    public void send(DatagramPacket packet, DatagramSocket socket) throws IOException {

        //String input = "Hello World!";
        CRC32 crc = new CRC32();
        crc.update(packet.getData());
        System.out.println("CRC32:"+ crc.getValue());

        socket.send(packet);

        //System.out.println("Data send");
        if(next != null)
        {
            next.send(packet, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket) throws IOException {
        socket.receive(packet);

        //wtf ça marche???
        CRC32 crc = new CRC32();
        String test = new String(packet.getData(), 0, packet.getLength());
        crc.update(test.getBytes());
        System.out.println("CRC32:"+ crc.getValue());

        //System.out.println("Data receive");
        if(previous != null)
        {
            previous.receive(packet, socket);
        }
    }
}
