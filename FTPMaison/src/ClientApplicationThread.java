import ChainOfResponsibility.ApplicationLayer;
import ChainOfResponsibility.DataLinkLayer;
import ChainOfResponsibility.TransportLayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientApplicationThread extends Thread{
    private static boolean isActive = true;
    private static TransportLayer transportLayer;
    private static DataLinkLayer dataLinkLayer;
    private static ApplicationLayer applicationLayer;
    private static DatagramSocket socket = null;

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
    }

    public void run() {

        while (isActive) {
            try {
                byte[] buf = new byte[200];
                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                dataLinkLayer.receive(packet, socket);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        socket.close();
    }

    public void sendFile(String fileName, InetAddress address) throws IOException {
        byte[] buf = new byte[200];

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);

        applicationLayer.send(packet, socket, fileName);
    }
}
