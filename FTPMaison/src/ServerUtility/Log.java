package ServerUtility;

public class Log {
    private int transmittedPacket;
    private int receivedPacket;
    private int lostPacket;
    private int crcPacketError;
    private String filename;

    public Log(String fileName) {
        this.transmittedPacket = 0;
        this.receivedPacket = 0;
        this.lostPacket = 0;
        this.crcPacketError = 0;
        this.filename = fileName;
    }

    public int getTransmittedPacket() {
        return this.transmittedPacket;
    }

    public void addTransmittedPacket() {
        transmittedPacket++;
    }

    public int getReceivedPacket() {
        return this.receivedPacket;
    }

    public void addReceivedPacket() {
        receivedPacket++;
    }

    public int getLostPacket() {
        return this.lostPacket;
    }

    public void addLostPacket() {
        lostPacket++;
    }

    public int getcrcPacketError() {
        return this.crcPacketError;
    }

    public void addcrcPacketError() {
        crcPacketError++;
    }
}
