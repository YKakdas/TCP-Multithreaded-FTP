package ftp;

import java.net.InetAddress;
import java.net.Socket;

/**
 * This class creates a new instance of FTP Client. You may run as many instance as you want, FTP Server will be able to
 * handle all simultaneously
 */
public class FTPClient {

    public FTPClient() {
        Socket socket;
        try {
            String serverAddress = "127.0.0.1";
            int serverPort = 4473;

            socket = new Socket(InetAddress.getByName(serverAddress), serverPort);

            FTPUtil.promptClientInput(socket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FTPClient();
    }
}
