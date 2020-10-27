package ChainOfResponsibility;

import ServerUtility.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class ApplicationLayer extends Layer{
    private String folderName;

    public ApplicationLayer(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName, String error) throws IOException {
        File file = new File(folderName + "/" + fileName);
        byte[] fileData = Files.readAllBytes(file.toPath());

        if(next != null)
        {
            next.send(packet, socket, fileName, fileData, error);
        }
    }

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
