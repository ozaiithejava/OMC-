package ozaii.client.discord.commands;


import ozaii.client.discord.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private static final Map<String, Command> commands = new HashMap<>();

    /**
     * Registers a command to the registry.
     *
     * @param command The command to register.
     */
    public static void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    /**
     * Retrieves a command by name.
     *
     * @param name The name of the command.
     * @return The corresponding command, or null if not found.
     */
    public static Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    /**
     * Checks if a command exists in the registry.
     *
     * @param name The command name.
     * @return True if the command exists, false otherwise.
     */
    public static boolean hasCommand(String name) {
        return commands.containsKey(name.toLowerCase());
    }
}
