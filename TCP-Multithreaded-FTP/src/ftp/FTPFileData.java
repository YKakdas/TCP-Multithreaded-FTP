package ftp;

import java.io.Serializable;

/**
 * This class is responsible for holding file data to be transmitted as well as its name.
 */
public class FTPFileData implements Serializable {
    private String fileName;
    private byte[] fileContent;

    public FTPFileData(String fileName, byte[] fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}
