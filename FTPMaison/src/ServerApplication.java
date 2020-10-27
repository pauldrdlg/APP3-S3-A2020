import java.io.IOException;

public class ServerApplication {
    private static ServerApplicationThread serverApplicationThread;
    public static void main(String[] args) throws IOException {
        serverApplicationThread = new ServerApplicationThread();
        serverApplicationThread.start();
    }
}
