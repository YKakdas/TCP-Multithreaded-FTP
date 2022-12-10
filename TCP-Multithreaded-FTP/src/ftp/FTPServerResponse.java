package ftp;

import java.io.Serializable;


/**
 * This class is responsible for carrying server responds to server side. Response type could be multiple things. It might
 * be a list if the command is "ls", it could be a FTPFileData instance if command is "get"
 */
public class FTPServerResponse implements Serializable {
    private CommandType commandType;
    private Object response;

    public FTPServerResponse(CommandType commandType, Object response) {
        this.commandType = commandType;
        this.response = response;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
