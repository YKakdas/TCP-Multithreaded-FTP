package ftp;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

/**
 * This Server Worker Thread receives commands from clients, parses and properly responses back to them.
 */
public class FTPServerWorkerThread extends Thread {

    private ServerSocket serverSocket;
    private String currentPath;

    public FTPServerWorkerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        ObjectOutputStream output;
        ObjectInputStream input;
        try {
            // Wait for a client to connect
            Socket socket = serverSocket.accept();
            System.out.println("An FTP Client has been established a connection...");

            // Keep track of current working directory. "cd" command can update it.
            currentPath = new File(".").getCanonicalPath();

            // Input-Output streams for communication over tcp socket
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            // While there exist a request from the client, loop
            FTPClientRequest request;
            while ((request = FTPUtil.waitClientCommand(input)) != null)
                switch (request.getCommandType()) {
                    // If command is "cwd", send current working directory path to client
                    case CWD -> FTPUtil.serverRespondCurrentWorkingDirectory(output, currentPath);
                    // If command is "cd", check if the given directory exist, if so, update the current working directory
                    case CD -> {
                        String dirPath = request.getCommandParameter();
                        String temp = currentPath + "\\" + dirPath;
                        if (Files.isDirectory(new File(temp).toPath())) {
                            currentPath = temp;
                        }
                    }
                    // If command is "ls", list all files and directories of one level under cwd
                    case LS -> FTPUtil.serverResponseLs(output, currentPath);
                    // If command is "get" and given filename exists in the working directory, send the file to client
                    case GET -> {
                        String filePath = currentPath + "\\" + request.getCommandParameter();
                        File file = new File(filePath);
                        if (Files.exists(file.toPath())) {
                            FTPUtil.serverRespondGetFile(output, file, request.getCommandParameter());
                        } else {
                            System.out.println("Couldn't respond client's get request. No such a file exists.");
                            FTPUtil.serverRespondGetFile(output, null, request.getCommandParameter());
                        }
                    }
                    // Client wants to terminate the connection. Close all streams.
                    case EXIT -> {
                        input.close();
                        output.close();
                        socket.close();
                        System.out.println("One of the connections has been terminated...");
                        return;
                    }

                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
