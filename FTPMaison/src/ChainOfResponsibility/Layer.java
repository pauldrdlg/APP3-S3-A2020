package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public abstract class Layer {
    protected Layer next;

    public void setNext(Layer nextLayer) {
            next = nextLayer;
    }

    public abstract void send(DatagramPacket packet, DatagramSocket socket) throws IOException;
    public abstract void receive(DatagramPacket packet, DatagramSocket socket) throws IOException;
}
