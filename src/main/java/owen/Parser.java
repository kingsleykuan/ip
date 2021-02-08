package owen;

import owen.command.Command;
import owen.command.CommandType;

/**
 * Parser responsible for parsing commands.
 */
public class Parser {
    /**
     * Parses command from a string.
     * 
     * @param commandString String to parse command from.
     * @return Parsed Command.
     * @throws OwenException Command cannot be parsed from string.
     */
    public static Command parseCommand(String commandString) throws OwenException {
        String[] commandStringSplit = commandString.split(" ", 2);
        String commandTypeString = commandStringSplit[0];

        // Try converting command to enum
        CommandType commandType;
        try {
            commandType = CommandType.valueOf(commandTypeString.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new OwenException("I'm sorry, but I don't know what that means...");
        }

        String args;
        if (commandStringSplit.length > 1) {
            args = commandStringSplit[1];
        } else {
            args = "";
        }

        return new Command(commandType, args, commandString);
    }
}
