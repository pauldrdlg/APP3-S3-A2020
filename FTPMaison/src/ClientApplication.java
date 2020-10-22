/*
* Commande pour Paul
* cd C:\Users\Utilisateur\Documents\GitHub Repository\APP3-S3-A2020\FTPMaison\out\production\FTPMaison
* java ClientApplication
*
* Commande pour Olivier
* cd "C:\Users\Olivier Lortie\Documents\UdeS-A20\APP 3\APP3-S3-A2020\FTPMaison\out\production\FTPMaison"
* java ClientApplication
* */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ChainOfResponsibility.*;

public class ClientApplication {
    private static TransportLayer transportLayerSender;
    private static DataLinkLayer dataLinkLayerSender;
    private static TransportLayer transportLayerReceiver;
    private static DataLinkLayer dataLinkLayerReceiver;
    private static DatagramSocket socket = null;


    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }

        transportLayerSender = new TransportLayer();
        dataLinkLayerSender = new DataLinkLayer();
        transportLayerReceiver = new TransportLayer();
        dataLinkLayerReceiver = new DataLinkLayer();

        transportLayerSender.setNext(dataLinkLayerSender);
        dataLinkLayerReceiver.setNext(transportLayerReceiver);

        // get a datagram socket
        socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[256];
        InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);

        transportLayerSender.send(packet, socket);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        dataLinkLayerReceiver.receive(packet, socket);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Quote of the Moment: " + received);

        socket.close();
    }
}
