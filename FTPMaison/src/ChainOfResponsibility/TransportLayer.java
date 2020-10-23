package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class TransportLayer extends Layer{
    @Override
    public void send(DatagramPacket packet, DatagramSocket socket, String filename, byte[] buf) throws IOException{

        packet.setData(buf, 0, buf.length);

        //System.out.println("Tansport send");

        if(next != null)
        {
            next.send(packet, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket) throws IOException {

        //System.out.println("Transport receive");

        String filename = "testDestination.txt";
        byte[] completedFile = packet.getData();

        if(previous != null)
        {
            previous.receive(packet, socket, filename, completedFile);
        }
    }
}
