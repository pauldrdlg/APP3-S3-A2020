package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public abstract class Layer {
    protected Layer next;
    protected Layer previous;

    public void setNext(Layer nextLayer) {
            next = nextLayer;
    }

    public void setPrevious(Layer previousLayer) {
        previous = previousLayer;
    }

    public abstract void send(DatagramPacket packet, DatagramSocket socket) throws IOException;
    public abstract void receive(DatagramPacket packet, DatagramSocket socket) throws IOException;
}
