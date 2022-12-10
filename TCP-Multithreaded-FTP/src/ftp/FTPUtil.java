package ftp;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FTPUtil {

    // FTP Server waits a client to send a command
    public static FTPClientRequest waitClientCommand(ObjectInputStream inputStream) {
        FTPClientRequest clientRequest = null;
        try {
            clientRequest = (FTPClientRequest) inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (clientRequest);
    }

    // FTP Client waits a response for its command. Client knows the response type by parsing command type
    private static void waitServerResponse(ObjectInputStream inputStream) {
        FTPServerResponse serverResponse = null;
        try {
            // If command is "cwd", the response of server should be a single String. Print it.
            serverResponse = (FTPServerResponse) inputStream.readObject();
            if (serverResponse.getCommandType() == CommandType.CWD) {
                System.out.println(serverResponse.getResponse());
                System.out.println();
                // If command is "ls", the response of server should be a list. Print the list.
            } else if (serverResponse.getCommandType() == CommandType.LS) {
                String[] files = (String[]) serverResponse.getResponse();
                printFiles(files);
                System.out.println();
                // If command is "get", the response of server should be a file and its name. Output file content into
                // a file with given name.
            } else if (serverResponse.getCommandType() == CommandType.GET) {
                FTPFileData data = (FTPFileData) serverResponse.getResponse();
                FileOutputStream fos = new FileOutputStream(data.getFileName());
                fos.write(data.getFileContent());
                System.out.println("The file named " + data.getFileName() + " has been successfully saved into you local directory");
            }
            // There is no other response type, something must have gone wrong if an error was thrown
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Print the list of filenames
    private static void printFiles(String[] files) {
        Arrays.stream(files).toList().forEach(System.out::println);
    }

    // Client sends a command for getting current working directory of the server
    private static void clientRequestCurrentWorkingDirectory(ObjectOutputStream output) {
        try {
            FTPClientRequest clientRequest = new FTPClientRequest(CommandType.CWD, null);
            output.writeObject(clientRequest);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Server sends current working directory to client
    public static void serverRespondCurrentWorkingDirectory(ObjectOutputStream output, String path) {
        try {
            FTPServerResponse serverResponse = new FTPServerResponse(CommandType.CWD, path);
            output.writeObject(serverResponse);
            output.flush();
            System.out.println("Current working directory has been sent to a client");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Client sends "ls" command to get a list of filenames exist in current working directory of the server
    private static void clientRequestLsCommand(ObjectOutputStream output) {
        try {
            FTPClientRequest clientRequest = new FTPClientRequest(CommandType.LS, null);
            output.writeObject(clientRequest);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Server gets all files and directories that exist in its cwd, puts them in a list and sends back to client.
    // "/" is added before directories to distinguish a file from a directory
    public static void serverResponseLs(ObjectOutputStream output, String currentPath) {
        try {
            ArrayList<String> fileNames = new ArrayList<>();
            Files.list(new File(currentPath).toPath())
                    .forEach(file -> {
                        if (Files.isDirectory(file)) {
                            fileNames.add(file.getFileName() + "/");
                        } else {
                            fileNames.add(file.getFileName().toString());
                        }
                    });
            FTPServerResponse serverResponse = new FTPServerResponse(CommandType.LS, fileNames.toArray(String[]::new));
            output.writeObject(serverResponse);
            output.flush();
            System.out.println("List of names of files and directories under the current " +
                    "working directory has been sent to a client");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Client requests to bring a file from server to its local directory.
    private static void clientRequestGetCommand(ObjectOutputStream output, String fileName) {
        try {
            FTPClientRequest clientRequest = new FTPClientRequest(CommandType.GET, fileName);
            output.writeObject(clientRequest);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Server sends a file from its cwd to client
    public static void serverRespondGetFile(ObjectOutputStream output, File file, String fileName) {
        try {
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            int count = fis.read(bytes);
            FTPServerResponse response = new FTPServerResponse(CommandType.GET, new FTPFileData(fileName, bytes));
            output.writeObject(response);
            output.flush();
            System.out.println("A file with name " + fileName + " has been sent to a client");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Client requests to change current working directory of server
    private static void clientRequestCdCommand(ObjectOutputStream output, String fileName) {
        try {
            FTPClientRequest clientRequest = new FTPClientRequest(CommandType.CD, fileName);
            output.writeObject(clientRequest);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Client notifies server that it terminated the connection
    private static void clientSendExit(ObjectOutputStream output) {
        try {
            FTPClientRequest clientRequest = new FTPClientRequest(CommandType.EXIT, null);
            output.writeObject(clientRequest);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Console manager of the client. Parses user prompts, verifies that they are valid commands.
    public static void promptClientInput(Socket socket) {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("\n");
            System.out.println("A connection to a FTP Server has been established successfully.");

            Scanner sc = new Scanner(System.in);
            String promptText = """
                    Please enter the command(s) you want to run in the server.
                    Available options are:
                        get <remote_filename>: Brings the specified file from remote into your local directory
                        ls: Lists files and directories of server's current directory
                        cd <directory_name>: Changes directory of server to specified path
                        cwd: To get server's current working directory's path
                        exit: Terminate the connection
                    """;
            System.out.println(promptText);
            while (true) {
                String command = sc.nextLine();
                String[] split = command.split("\\s+");
                // At this moment, the longest command should be in two word length, longer than this, should be ignored
                if (split.length > 2) {
                    System.out.println("Unknown command... Please try again!");
                    continue;
                } else {
                    // If a command is only a single word, it must be one of the "exit", "ls", or "cwd" commands
                    if (split.length == 1) {
                        if (split[0].toLowerCase().contentEquals("exit")) {
                            clientSendExit(output);
                            input.close();
                            output.close();
                            socket.close();
                            System.exit(0);
                        } else if (split[0].toLowerCase().contentEquals("ls")) {
                            clientRequestLsCommand(output);
                            waitServerResponse(input);
                        } else if (split[0].toLowerCase().contentEquals("cwd")) {
                            clientRequestCurrentWorkingDirectory(output);
                            waitServerResponse(input);
                        } else {
                            System.out.println("Unknown command... Please try again!");
                            continue;
                        }
                    } else {
                        // If the command is two-words, it should either be a "get" or "cd" command
                        if (split[0].toLowerCase().contentEquals("get")) {
                            clientRequestGetCommand(output, split[1]);
                            waitServerResponse(input);
                        } else if (split[0].toLowerCase().contentEquals("cd")) {
                            clientRequestCdCommand(output, split[1]);
                        } else {
                            System.out.println("Unknown command... Please try again!");
                            continue;
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
