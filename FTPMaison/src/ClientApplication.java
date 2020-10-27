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
import java.net.InetAddress;

/**
 * @author Paul du Réau et Olivier Lortie
 *
 * Cette classe gère les arguments d'entrée du client du protocole maison et le début du thread
 */
public class ClientApplication {
    private static ClientApplicationThread clientApplicationThread;
    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.out.println("Il manque un parametre pour designer le destinataire: ClientApplication ligne 23");
            return;
        }

        clientApplicationThread = new ClientApplicationThread();
        clientApplicationThread.sendFile(args[0], InetAddress.getByName(args[1]), args[2]);
        clientApplicationThread.start();
    }
}
