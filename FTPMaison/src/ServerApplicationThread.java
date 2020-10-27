import ChainOfResponsibility.*;
import ServerUtility.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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
