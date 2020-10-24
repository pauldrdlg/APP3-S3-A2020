package ChainOfResponsibility;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ApplicationLayer extends Layer{
    @Override
    public void send(DatagramPacket packet, DatagramSocket socket, String fileName) throws IOException {
        /*File file = new File(fileName);
        byte[] fileData = new byte[(int) file.length()];
        FileInputStream in = new FileInputStream(file);
        in.read(fileData);
        in.close();

        fileName = fileName.split("/")[1];*/


        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(fileName));
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
        //packet.setData(message.getBytes(), 0, message.getBytes().length);

        if(next != null)
        {
            next.send(packet, socket, fileName, fileData);
        }
    }

    @Override
    public void receive(DatagramPacket packet, DatagramSocket socket, String filename, byte[] buf) throws IOException {
        String message = new String(buf, 0, buf.length);
        System.out.println(message);

        String filepath = "DestinationFolder/" + filename;
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

        //System.out.println("Application receive");
        if(previous != null)
        {
            previous.receive(packet, socket);
        }
    }
}
