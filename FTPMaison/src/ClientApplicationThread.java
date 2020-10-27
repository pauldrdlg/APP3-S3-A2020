import ChainOfResponsibility.*;
import ServerUtility.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Paul du Réau et Olivier Lortie
 *
 * Cette classe gère le thread du client du protocole maison
 */
public class ClientApplicationThread extends Thread{
    private static boolean run = true;
    private static TransportLayer transportLayer;
    private static DataLinkLayer dataLinkLayer;
    private static ApplicationLayer applicationLayer;
    private static DatagramSocket socket = null;
    private static Log log;

    public ClientApplicationThread() throws IOException {
        this("ClientApplicationThread");
    }

    /**
     * Constructeur du thread
     *
     * @param name Nom du thread
     * @throws IOException
     */
    public ClientApplicationThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket();

        transportLayer = new TransportLayer();
        dataLinkLayer = new DataLinkLayer();
        applicationLayer = new ApplicationLayer("SourceFolder");

        applicationLayer.setNext(transportLayer);
        transportLayer.setNext(dataLinkLayer);
        dataLinkLayer.setPrevious(transportLayer);
        transportLayer.setPrevious(applicationLayer);

        log = new Log("ClientLiaisonDeDonnees.log");
    }

    /**
     * Cette fonction sera exécutée au début du thread et permet d'écouter la reception des fichiers ACK
     */
    public void run() {
        while (run) {
            try {
                byte[] buf = new byte[200];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                dataLinkLayer.receive(packet, socket, log);

            } catch (IOException | TransmissionErrorException e) {
                e.printStackTrace();
                run = false;
            }
        }
        socket.close();
    }

    /**
     * Débute le processus d'envoi du fichier vers le serveur
     *
     * @param fileName Nom du fichier à lire
     * @param address Adresse du serveur
     * @param error Paramètre de simulation d'erreur (0, 1, 2 ou 3)
     * @throws IOException
     */
    public void sendFile(String fileName, InetAddress address, String error) throws IOException {
        byte[] buf = new byte[200];

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 25567);

        applicationLayer.send(packet, socket, fileName, error);
    }
}
