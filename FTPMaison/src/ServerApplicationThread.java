import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import ChainOfResponsibility.*;
import ServerUtility.*;

public class ServerApplicationThread extends Thread{
    protected DatagramSocket socket = null;
    private boolean moreQuotes = true;

    private TransportLayer transportLayer;
    private DataLinkLayer dataLinkLayer;
    private ApplicationLayer applicationLayer;

    private Log log;

    public ServerApplicationThread() throws IOException {
        this("ServerApplicationThread");
    }

    public ServerApplicationThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);

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
        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                dataLinkLayer.receive(packet, socket, log);

                // figure out response
                /*String dString = null;
                if (in == null)
                    dString = new Date().toString();
                else
                    dString = getNextQuote();
                String dString = "Message du serveur!";
                buf = dString.getBytes();*/

                // send the response to the client at "address" and "port"
                /*InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);

                applicationLayer.send(packet, socket);*/
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
        socket.close();
    }
}
