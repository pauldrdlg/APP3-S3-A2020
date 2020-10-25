import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import ChainOfResponsibility.*;

public class ServerApplicationThread extends Thread{
    protected DatagramSocket socket = null;
    //protected BufferedReader in = null;
    protected boolean moreQuotes = true;

    private TransportLayer transportLayer;
    private DataLinkLayer dataLinkLayer;
    private ApplicationLayer applicationLayer;

    public ServerApplicationThread() throws IOException {
        this("ServerApplicationThread");

        transportLayer = new TransportLayer();
        dataLinkLayer = new DataLinkLayer();
        applicationLayer = new ApplicationLayer("DestinationFolder");

        applicationLayer.setNext(transportLayer);
        transportLayer.setNext(dataLinkLayer);
        dataLinkLayer.setPrevious(transportLayer);
        transportLayer.setPrevious(applicationLayer);
    }

    public ServerApplicationThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);

        /*try {
            in = new BufferedReader(new FileReader("test.txt"));
        } catch (FileNotFoundException e) {

            System.err.println("Could not open quote file. Serving time instead." + e);
        }*/
    }

    public void run() {

        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                dataLinkLayer.receive(packet, socket);

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

    /*protected String getNextQuote() {
        String returnValue = null;
        try {
            if ((returnValue = in.readLine()) == null) {
                in.close();
                moreQuotes = false;
                returnValue = "No more quotes. Goodbye.";
            }
        } catch (IOException e) {
            returnValue = "IOException occurred in server.";
        }
        return returnValue;
    }*/
}
