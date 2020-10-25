package ChainOfResponsibility;

import ServerUtility.FileToSave;
import ServerUtility.Log;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class ApplicationLayer extends Layer{
    private String folderName;

    public ApplicationLayer(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName) throws IOException {

        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(folderName + "/" + fileName));
        } catch (FileNotFoundException e) {

            System.err.println("Could not open file. Serving time instead." + e);
        }

        String message = "", temp = "";

        while (((temp = in.readLine())) != null) {
            message += temp + "\n";
        }
        message = message.substring(0, message.length() - 1);
        in.close();
        byte[] fileData = message.getBytes();

        if(next != null)
        {
            next.send(packet, socket, fileName, fileData);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket, Log log, FileToSave fileToSave) throws IOException {
        byte[] numberBytes = separateByteArrays(28, 31, packet.getData());
        int number = ByteBuffer.wrap(numberBytes).getInt();

        if(fileToSave.getNbPacket() == number)
        {
            String filepath = folderName + "/" + fileToSave.getFilename();
            File file = new File(filepath);

            try {
                // Initialize a pointer
                // in file using OutputStream
                OutputStream os = new FileOutputStream(file);

                // Starts writing the bytes in it
                os.write(fileToSave.getData().getBytes());

                // Close the file
                os.close();
            }
            catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }

        if(previous != null)
        {
            previous.receive(packet, socket, log, fileToSave);
        }
    }
}
