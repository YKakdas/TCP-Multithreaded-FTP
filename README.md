# TCP-Multithreaded-FTP

This application is a simple demonstration of multithreaded File Transfer Protocol (FTP) by utilizing TCP sockets.

To run the FTP Server

```
java FTPMultithreadedServer
```

To run the FTP Client(s), as many as you want,

```
java FTPClient
```

Supported commands from client to server:

-   get <remote_filename>: Brings the specified file from remote into your local directory
-   ls: Lists files and directories of server's current directory
-   cd <directory_name>: Changes directory of server to specified path
-   cwd: To get server's current working directory's path
-   exit: Terminate the connection