package ServerUtility;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Paul du Réau et Olivier Lortie
 *
 * Cette classe gère la création des logs du protocole maison et les statistiques
 * liés à la réception des packet et des erreurs dans les packets
 */
public class Log {
    private int receivedPacket;
    private int lostPacket;
    private int crcPacketError;

    private Logger logger;

    /**
     * Constructeur du log
     *
     * @param fileName Nom du fichier du log
     */
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
            logger.setUseParentHandlers(false);

            logger.info("Start of the log");
        }
        catch(Exception e)
        {
            System.out.println("Error in the creation of the log: " + fileName + " || " + e.getMessage());
        }
    }

    /**
     * @return Les statistiques des packets reçus
     */
    public int getReceivedPacket() {
        return receivedPacket;
    }

    /**
     * Ajoute un packet reçu aux statistiques et écrit dans le log le total de packet reçu
     */
    public void addReceivedPacket() {
        receivedPacket++;
        logger.info("Total packet received: " + receivedPacket);
    }

    /**
     * @return Les statistiques des packets perdu
     */
    public int getLostPacket() {
        return lostPacket;
    }

    /**
     * Ajoute un packet perdu aux statistiques et écrit dans le log le total de packet perdu
     */
    public void addLostPacket() {
        lostPacket++;
        logger.warning("Total lost packet: " + lostPacket + "!");
    }

    /**
     * @return Les statistiques des packets avec erreur de CRC
     */
    public int getcrcPacketError() {
        return crcPacketError;
    }

    /**
     * Ajoute un packet avec erreur de CRC aux statistiques et écrit dans le log le total de packet avec erreur de CRC
     */
    public void addcrcPacketError() {
        crcPacketError++;
        logger.warning("Total CRC packet error: " + crcPacketError + "!");
    }

    /**
     * @param message Message à ajouter dans le log
     */
    public void addInfoToLog(String message) {
        logger.info(message);
    }

    /**
     * @param message Warning à ajouter dans le log
     */
    public void addWarningToLog(String message) {
        logger.warning(message);
    }
}
