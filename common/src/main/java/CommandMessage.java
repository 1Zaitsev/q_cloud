public class CommandMessage extends AbstractMessage {

    private String fileName;
    private CommandType commandType;

    public String getFileName() {
        return fileName;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public CommandMessage(String fileName, CommandType commandType) {
        this.fileName = fileName;
        this.commandType = commandType;
    }
}
