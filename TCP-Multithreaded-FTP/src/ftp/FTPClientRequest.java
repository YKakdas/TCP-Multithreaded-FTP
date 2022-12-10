package ftp;

import java.io.Serializable;

/**
 * This class is responsible for carrying client commands to server side. Any additional parameter such as filename if
 * the command is GET carried by commandParameter.
 */
public class FTPClientRequest implements Serializable {
    private CommandType commandType;
    private String commandParameter;

    public FTPClientRequest(CommandType commandType, String commandParameter) {
        this.commandType = commandType;
        this.commandParameter = commandParameter;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public String getCommandParameter() {
        return commandParameter;
    }

    public void setCommandParameter(String commandParameter) {
        this.commandParameter = commandParameter;
    }
}

