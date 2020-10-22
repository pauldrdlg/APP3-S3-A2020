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

    private static TransportLayer transportLayer;
    private static DataLinkLayer dataLinkLayer;
    private static ApplicationLayer applicationLayer;
    private static DatagramSocket socket = null;


    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Il manque un parametre pour designer le destinataire: ClientApplication ligne 29");
            return;
        }

        applicationLayer = new ApplicationLayer();
        transportLayer = new TransportLayer();
        dataLinkLayer = new DataLinkLayer();

        applicationLayer.setNext(transportLayer);
        transportLayer.setNext(dataLinkLayer);
        dataLinkLayer.setPrevious(transportLayer);
        transportLayer.setPrevious(applicationLayer);

        // get a datagram socket
        socket = new DatagramSocket();

        // send request
        byte[] buf = ("Message du client!").getBytes();
        InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);

        applicationLayer.send(packet, socket);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        dataLinkLayer.receive(packet, socket);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Message received: " + received);

        socket.close();
    }
}
