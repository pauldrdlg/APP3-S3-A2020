package ChainOfResponsibility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ApplicationLayer extends Layer{
    @Override
    public void send(DatagramPacket packet, DatagramSocket socket) throws IOException {
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader("test.txt"));
        } catch (FileNotFoundException e) {

            System.err.println("Could not open file. Serving time instead." + e);
        }

        String message = "", temp = "";

        while (((temp = in.readLine())) != null) {
            message += temp + "\n";
        }
        message = message.substring(0, message.length() - 1);
        in.close();

        //packet.setData(message.getBytes(), 0, message.getBytes().length);

        if(next != null)
        {
            next.send(packet, socket);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket) throws IOException {
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println(message);

        //System.out.println("Application receive");
        if(previous != null)
        {
            previous.receive(packet, socket);
        }
    }
}
