import ChainOfResponsibility.*;
import ServerUtility.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

    public void sendFile(String fileName, InetAddress address, String error) throws IOException {
        byte[] buf = new byte[200];

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 25567);

        applicationLayer.send(packet, socket, fileName, error);
    }
}
