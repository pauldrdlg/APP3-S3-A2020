package ServerUtility;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
    private int receivedPacket;
    private int lostPacket;
    private int crcPacketError;

    private Logger logger;

    public Log(String fileName) {
        receivedPacket = 0;
        lostPacket = 0;
        crcPacketError = 0;

        try
        {
            logger = Logger.getLogger("Cool Log");
            FileHandler fh = new FileHandler("Logs/" + fileName);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            //True si tu veux voir les logs dans la console, false si tu ne veux pas les voir
            logger.setUseParentHandlers(true);

            logger.info("Start of the log");
        }
        catch(Exception e)
        {
            System.out.println("Error in the creation of the log: " + fileName + " || " + e.getMessage());
        }
    }

    public int getReceivedPacket() {
        return receivedPacket;
    }

    public void addReceivedPacket() {
        receivedPacket++;
        logger.info("Total packet received: " + receivedPacket);
    }

    public int getLostPacket() {
        return lostPacket;
    }

    public void addLostPacket() {
        lostPacket++;
        logger.warning("Total lost packet: " + lostPacket + "!");
    }

    public int getcrcPacketError() {
        return crcPacketError;
    }

    public void addcrcPacketError() {
        crcPacketError++;
        logger.warning("Total CRC packet error: " + crcPacketError + "!");
    }

    public void addInfoToLog(String message) {
        logger.info(message);
    }

    public void addWarningToLog(String message) {
        logger.warning(message);
    }
}
