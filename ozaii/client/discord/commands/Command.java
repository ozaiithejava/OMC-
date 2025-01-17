package ozaii.client.discord.commands;


import org.javacord.api.event.message.MessageCreateEvent;

public interface Command {
    /**
     * Executes the command logic.
     *
     * @param event The message create event triggering the command.
     * @param args  The arguments provided with the command.
     */
    void execute(MessageCreateEvent event, String[] args);

    /**
     * Returns the name of the command.
     *
     * @return Command name.
     */
    String getName();
}
