package ftp;

/**
 * Supported operations between FTP Server & Client
 * <ul>
 * <li> CWD : Prints current working directory of the server
 * <li> CD : Changes current directory of server to specified directory
 * <li> LS : Lists the files and directories exist under current directory
 * <li> GET : Transmits a file from the server to client
 * <li> EXIT: Terminates connection
 * </ul>
 */
public enum CommandType {
    CWD,
    CD,
    LS,
    GET,
    EXIT
}
