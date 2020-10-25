package ServerUtility;

public class Log {
    private int transmittedPacket;
    private int receivedPacket;
    private int lostPacket;
    private int crcPacketError;
    private final String filename = "liaisonDeDonnees.log";

    public Log() {
        transmittedPacket = 0;
        receivedPacket = 0;
        lostPacket = 0;
        crcPacketError = 0;
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
