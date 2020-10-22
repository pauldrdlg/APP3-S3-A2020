package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DataLinkLayer extends Layer{
    @Override
    public void send(DatagramPacket packet, DatagramSocket socket) throws IOException {
        socket.send(packet);

        System.out.println("Data send");
        if(next != null)
        {
            next.send(packet, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket) throws IOException {
        socket.receive(packet);

        System.out.println("Data receive");
        if(next != null)
        {
            next.receive(packet, socket);
        }
    }
}
