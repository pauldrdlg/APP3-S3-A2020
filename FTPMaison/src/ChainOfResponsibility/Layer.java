package ChainOfResponsibility;

import ServerUtility.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author Paul du Réau et Olivier Lortie
 *
 * Cette classe gère par abstraction les couches du protocole maison
 * Fait partie du pattern de la chaine de responsabilité
 * Possède les fonctions et variables communes aux trois couches
 */
public abstract class Layer {
    protected Layer next;
    protected Layer previous;

    protected static final int messageSize = 20;
    protected static final int numberSize = 4;
    protected static final int crcSize = 8;
    protected static final int dataSize = 168;

    /**
     * Définit la prochaine couche dans la chaine de responsabilité
     *
     * @param nextLayer Prochaine couche dans la chaine de responsabilité
     */
    public void setNext(Layer nextLayer) {
            next = nextLayer;
    }

    /**
     * Définit la précédente couche dans la chaine de responsabilité
     *
     * @param previousLayer Précédente couche dans la chaine de responsabilité
     */
    public void setPrevious(Layer previousLayer) {
        previous = previousLayer;
    }

    /**
     * Définit la fonction d'envoi des packets
     *
     * @param packet Packet contenant l'adresse et le port
     * @param socket Socket qui envoie la packet
     * @param error Paramètre de simulation d'erreur (0, 1, 2 ou 3)
     * @throws IOException
     */
    public void send(DatagramPacket packet, DatagramSocket socket, String error) throws IOException {
        if (next != null) {
            next.send(packet, socket, error);
        }
    }

    /**
     * Définit la fonction d'envoi des packets
     *
     * @param packet Packet contenant l'adresse et le port
     * @param socket Socket qui envoie la packet
     * @param fileName Nom du fichier
     * @param error Paramètre de simulation d'erreur (0, 1, 2 ou 3)
     * @throws IOException
     */
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName, String error) throws IOException {
        if (next != null) {
            next.send(packet, socket, error);
        }
    }

    /**
     * Définit la fonction d'envoi des packets
     *
     * @param packet Packet contenant l'adresse et le port
     * @param socket Socket qui envoie la packet
     * @param fileName Nom du fichier
     * @param buf Tableau de byte qui contient le fichier à écrire
     * @param error Paramètre de simulation d'erreur (0, 1, 2 ou 3)
     * @throws IOException
     */
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName, byte[] buf, String error) throws IOException {
        if (next != null) {
            next.send(packet, socket, error);
        }
    }

    /**
     * Définit la fonction de réception des packets
     *
     * @param packet Packet contenant l'adresse et le port
     * @param socket Socket qui envoie la packet
     * @param log Paramètre pour écrire dans les logs
     * @throws IOException
     * @throws TransmissionErrorException
     */
    public void receive(DatagramPacket packet, DatagramSocket socket, Log log) throws IOException, TransmissionErrorException {
        if (previous != null) {
            previous.receive(packet, socket, log);
        }
    }

    /**
     * Définit la fonction de réception des packets
     *
     * @param packet Packet contenant l'adresse et le port
     * @param socket Socket qui envoie la packet
     * @param log Paramètre pour écrire dans les logs
     * @param fileName Nom du fichier
     * @param buf Tableau de byte qui contient le fichier à écrire
     * @throws IOException
     * @throws TransmissionErrorException
     */
    public void receive(DatagramPacket packet, DatagramSocket socket, Log log, String fileName, byte[] buf) throws IOException, TransmissionErrorException {
        if (previous != null) {
            previous.receive(packet, socket, log);
        }
    }

    /**
     * Enlève les zéros de la première occurence à la fin d'un tableau de bytes
     *
     * @param buf Le tableau de byte où il faut enlever les zéros
     * @return Le tableau avec les zéros enlevés
     */
    public byte[] trimZeros(byte[] buf) {
        String str = new String(buf, 0, buf.length);

        int pos = str.indexOf(0);

        return (pos == -1 ? str : str.substring(0, pos)).getBytes();
    }

    /**
     * Insert des zéros à la fin d'un tableau de bytes jusqu'a une longueur de la taille total
     *
     * @param totalSize Taille totale du tableau de bytes après l'ajout des zéros
     * @param buf Tableau de byte soù il faut ajouter les zéros
     * @return Le tableau avec les zéros ajoutés
     */
    public byte[] fillWithZeros(int totalSize, byte[] buf) {
        byte[] temp = new byte[totalSize];

        for (int i = 0; i < totalSize - (totalSize - buf.length); i++) {
            temp[i] = buf[i];
        }

        return temp;
    }

    /**
     * Création d'un tableau de byte composé du premier et du deuxième tableau
     *
     * @param buf1 Premier tableau
     * @param buf2 Deuxième tableau
     * @return Le tableau composé du premier et du deuxième tableau
     */
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

    /**
     * Création d'un tableau de bytes composé d'une partie définie d'un tableau de bytes initial
     *
     * @param startIndex Début de la partie définie
     * @param endIndex Fin de la partie définie
     * @param buf Tableau de bytes initial
     * @return Le tableau de bytes composé d'une partie définie du tableau de bytes initial
     */
    public byte[] separateByteArrays(int startIndex, int endIndex, byte[] buf) {
        byte[] temp = new byte[endIndex - startIndex + 1];

        for (int i = startIndex; i <= endIndex; i++) {
            temp[i - startIndex] = buf[i];
        }

        return temp;
    }

    /**
     * Insertion de bytes dans un tableau de bytes à un endroit précis
     *
     * @param start Début de l'insertion
     * @param end Fin de l'insertion
     * @param originalBuf Tableau de bytes original où il faut inserer des bytes
     * @param toInsert Tableau de bytes à insérer dans le tableau de bytes original
     * @return Le tableau original avec les bytes insérés
     */
    public byte[] writeIntoByteArrays(int start, int end, byte[] originalBuf, byte[] toInsert) {
        int count = 0;

        for(int i = start; i <= end; i++) {
            originalBuf[i] = toInsert[count];
            count++;
        }

        return originalBuf;
    }
}