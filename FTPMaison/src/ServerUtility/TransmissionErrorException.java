package ServerUtility;

/**
 * @author Paul du Réau et Olivier Lortie
 *
 * Cette classe gère l'exception de transmission du protocole maison
 */
public class TransmissionErrorException extends Exception{
    public TransmissionErrorException(String message) {
        super(message);
    }
}
