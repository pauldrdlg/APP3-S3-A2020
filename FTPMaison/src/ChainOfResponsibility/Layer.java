package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class Layer {
    protected Layer next;
    protected Layer previous;

    public void setNext(Layer nextLayer) {
            next = nextLayer;
    }

    public void setPrevious(Layer previousLayer) {
        previous = previousLayer;
    }

    public void send(DatagramPacket packet, DatagramSocket socket) throws IOException {
        if (next != null) {
            next.send(packet, socket);
        }
    }

    public void send(DatagramPacket packet, DatagramSocket socket, String fileName) throws IOException {
        if (next != null) {
            next.send(packet, socket);
        }
    }

    public void send(DatagramPacket packet, DatagramSocket socket, String fileName, byte[] buf) throws IOException {
        if (next != null) {
            next.send(packet, socket);
        }
    }


    public void receive(DatagramPacket packet, DatagramSocket socket) throws IOException {
        if (previous != null) {
            previous.receive(packet, socket);
        }
    }

    public void receive(DatagramPacket packet, DatagramSocket socket, String fileName, byte[] buf) throws IOException {
        if (previous != null) {
            previous.receive(packet, socket);
        }
    }
}
