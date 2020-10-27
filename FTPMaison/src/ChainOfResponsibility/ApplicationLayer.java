package ChainOfResponsibility;

import ServerUtility.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;

/**
 * @author Paul du Réau et Olivier Lortie
 *
 * Cette classe représente la couche d'application du protocole maison
 * Fait partie du pattern de la chaine de responsabilité
 * Elle lit et écrit dans des fichiers
 */
public class ApplicationLayer extends Layer{
    private String folderName;

    public ApplicationLayer(String folderName) {
        this.folderName = folderName;
    }

    /**
     * Étape de la couche application qui envoie des données
     * Le serveur ne vient jamais dans cette fonction
     *
     * @param packet Packet contenant l'adresse et le port
     * @param socket Socket qui envoie la packet
     * @param fileName Nom du fichier
     * @param error Paramètre de simulation d'erreur (0, 1, 2 ou 3)
     * @throws IOException
     */
    @Override
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName, String error) throws IOException {
        File file = new File(folderName + "/" + fileName);
        byte[] fileData = Files.readAllBytes(file.toPath());

        if(next != null)
        {
            next.send(packet, socket, fileName, fileData, error);
        }
    }

    /**
     * Étape de la couche application qui reçoie des données
     * Le client ne vient jamais dans cette fonction
     *
     * @param packet Packet contenant l'adresse et le port
     * @param socket Socket qui envoie la packet
     * @param log Paramètre pour écrire dans les logs
     * @param fileName Nom du fichier
     * @param buf Tableau de byte qui contient le fichier à écrire
     * @throws IOException
     * @throws TransmissionErrorException
     */
    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket, Log log, String fileName, byte[] buf) throws IOException, TransmissionErrorException {
        String filepath = folderName + "/" + fileName;
        File file = new File(filepath);

        try {
            // Initialize a pointer
            // in file using OutputStream
            OutputStream os = new FileOutputStream(file);

            // Starts writing the bytes in it
            os.write(buf);

            // Close the file
            os.close();
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }

        if(previous != null)
        {
            previous.receive(packet, socket, log);
        }
    }
}
