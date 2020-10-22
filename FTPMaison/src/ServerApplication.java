import java.io.IOException;

public class ServerApplication {

    public static void main(String[] args) throws IOException {
        new ServerApplicationThread().start();
    }
}
