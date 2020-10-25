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
    private static ClientApplicationThread clientApplicationThread;
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Il manque un parametre pour designer le destinataire: ClientApplication ligne 31");
            return;
        }

        clientApplicationThread = new ClientApplicationThread();
        clientApplicationThread.sendFile("test.txt", InetAddress.getByName(args[0]));
        clientApplicationThread.start();

    }
}
