import java.io.IOException;

/**
 * @author Paul du Réau et Olivier Lortie
 *
 * Cette classe gère le serveur du protocole maison et le début du thread
 */
public class ServerApplication {
    private static ServerApplicationThread serverApplicationThread;
    public static void main(String[] args) throws IOException {
        serverApplicationThread = new ServerApplicationThread();
        serverApplicationThread.start();
    }
}
