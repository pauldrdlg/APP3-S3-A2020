package ChainOfResponsibility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import ServerUtility.*;

public abstract class Layer {
    protected Layer next;
    protected Layer previous;

    protected static final int messageSize = 20;
    protected static final int numberSize = 4;
    protected static final int crcSize = 8;
    protected static final int dataSize = 168;

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


    public void receive(DatagramPacket packet, DatagramSocket socket, Log log) throws IOException {
        if (previous != null) {
            previous.receive(packet, socket, log);
        }
    }

    public void receive(DatagramPacket packet, DatagramSocket socket, Log log, String fileName, byte[] buf) throws IOException {
        if (previous != null) {
            previous.receive(packet, socket, log);
        }
    }


    public byte[] trimZeros(byte[] buf) {
        String str = new String(buf, 0, buf.length);

        int pos = str.indexOf(0);

        return (pos == -1 ? str : str.substring(0, pos)).getBytes();
    }

    public byte[] fillWithZeros(int totalSize, byte[] buf) {
        byte[] temp = new byte[totalSize];
        for (int i = 0; i < totalSize - (totalSize - buf.length); i++) {
            temp[i] = buf[i];
        }

        return temp;
    }

    public byte[] addByteArrays(byte[] buf1, byte[] buf2) {
        int length = buf1.length + buf2.length;
        byte[] temp = new byte[length];

        int pos = 0;
        for (byte element : buf1) {
            temp[pos] = element;
            pos++;
        }

        for (byte element : buf2) {
            temp[pos] = element;
            pos++;
        }

        return temp;
    }

    public byte[] separateByteArrays(int startIndex, int endIndex, byte[] buf) {
        byte[] temp = new byte[endIndex - startIndex + 1];

        for (int i = startIndex; i <= endIndex; i++) {
            temp[i - startIndex] = buf[i];
        }

        return temp;
    }

    public byte[] writeIntoByteArrays(int start, int end, byte[] originalBuf, byte[] toInsert) {
        int count = 0;

        for(int i = start; i <= end; i++)
        {
            originalBuf[i] = toInsert[count];
            count++;
        }

        return originalBuf;
    }
}
