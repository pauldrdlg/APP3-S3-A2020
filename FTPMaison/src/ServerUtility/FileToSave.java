package ServerUtility;

public class FileToSave {
    private String data;
    private int errors;
    private int nbPacket;
    private String filename;

    public FileToSave() {
        data = "";
        errors = 0;
        nbPacket = 0;
    }

    public String getData() {
        return data;
    }

    public void addData(String dataToAdd) {
        this.data += dataToAdd;
    }

    public int getErrors() {
        return errors;
    }

    public void addError() {
        this.errors++;
    }

    public int getNbPacket() {
        return nbPacket;
    }

    public void setNbPacket(int nbPacket) {
        this.nbPacket = nbPacket;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
