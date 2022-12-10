package ftp;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multithreaded FTP Server. Creates 8 threads at this point which means able to serve 8 clients simultaneously.
 */
public class FTPMultithreadedServer {

    public FTPMultithreadedServer() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(8);
            ServerSocket serverSocket = new ServerSocket(4473);

            for (int i = 0; i < 8; i++) {
                FTPServerWorkerThread thread =
                        new FTPServerWorkerThread(serverSocket);
                executorService.submit(thread);
            }
            executorService.shutdown();

            while (!executorService.isTerminated()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new FTPMultithreadedServer();
    }
}
