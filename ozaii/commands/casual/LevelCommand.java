package ozaii.commands.casual;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.apis.base.FactoryApi;

public class LevelCommand extends VanillaCommand {
    // Api class instance to retrieve level information
    static FactoryApi api = new FactoryApi();

    public LevelCommand() {
        // Define the command name, description, and usage message
        super("level");
        this.description = "See your or another player's current level";
        this.usageMessage = "/level [player-name]";
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // The command should only be used by players
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName;

            // If a player name is provided as an argument, show their level
            if (args.length > 0) {
                playerName = args[0];
            } else {
                // If no player name is provided, show the level of the player executing the command
                playerName = player.getName();
            }

            // Retrieve the level of the specified player asynchronously
            int level = (int) api.getLevelManager().getLevelAsync(playerName).join();

            // Format and display the table-like message with colors
            String message = "§6----------------------------\n" +  // Yellow border
                    "§ePlayer: §a" + playerName + "\n" +  // Green for player name
                    "§eLevel: §b" + level + "\n" +  // Blue for level
                    "§6----------------------------";  // Yellow border

            // Display the message to the player
            player.sendMessage(message);

        } else {
            // If the command is used by something other than a player, show an error message
            sender.sendMessage("§cThis command can only be used by players.");  // Red error message
        }

        return true;
    }
}
