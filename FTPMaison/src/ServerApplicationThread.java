import ChainOfResponsibility.*;
import ServerUtility.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author Paul du Réau et Olivier Lortie
 *
 * Cette classe gère le thread du serveur du protocole maison
 */
public class ServerApplicationThread extends Thread{
    protected DatagramSocket socket;
    private boolean run = true;

    private TransportLayer transportLayer;
    private DataLinkLayer dataLinkLayer;
    private ApplicationLayer applicationLayer;

    private Log log;

    public ServerApplicationThread() throws IOException {
        this("ServerApplicationThread");
    }

    /**
     * Constructeur du thread
     *
     * @param name Nom du thread
     * @throws IOException
     */
    public ServerApplicationThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(25567);

        transportLayer = new TransportLayer();
        dataLinkLayer = new DataLinkLayer();
        applicationLayer = new ApplicationLayer("DestinationFolder");

        applicationLayer.setNext(transportLayer);
        transportLayer.setNext(dataLinkLayer);
        dataLinkLayer.setPrevious(transportLayer);
        transportLayer.setPrevious(applicationLayer);

        log = new Log("ServerLiaisonDeDonnees.log");
    }

    /**
     * Cette fonction sera exécutée au début du thread et permet d'écouter la reception des fichiers de données
     */
    public void run() {
        while (run) {
            try {
                byte[] buf = new byte[256];

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
}
