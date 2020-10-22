package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class TransportLayer extends Layer{
    @Override
    public void send(DatagramPacket packet, DatagramSocket socket) throws IOException{

        /*byte[] buf = ("Olivier est le plus beau!!").getBytes();

        packet.setData(buf, 0, buf.length);*/

        //System.out.println("Tansport send");

        if(next != null)
        {
            next.send(packet, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket) throws IOException {


        //System.out.println("Transport receive");

        if(previous != null)
        {
            previous.receive(packet, socket);
        }
    }
}
