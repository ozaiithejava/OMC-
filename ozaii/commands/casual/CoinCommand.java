package ozaii.commands.casual;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.apis.base.FactoryApi;

public class CoinCommand extends VanillaCommand {
    // Api class instance to retrieve coin information
    static FactoryApi api = new FactoryApi();

    public CoinCommand() {
        // Define the command name, description, and usage message
        super("coin");
        this.description = "See your or another player's current coin balance";
        this.usageMessage = "/coin [player-name]";
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Check if the sender is a player (command should only be used by players)
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName;

            // If a player name is provided as an argument, display their coin balance
            if (args.length > 0) {
                playerName = args[0];
            } else {
                // If no player name is provided, show the balance of the player executing the command
                playerName = player.getName();
            }

            // Retrieve the coin balance for the specified player
            int coins = api.getCoinManager().getCoins(playerName).join().intValue();

            // Format and display the table-like message with colors
            String message = "§6----------------------------\n" +  // Yellow border
                    "§ePlayer: §a" + playerName + "\n" +  // Green for player name
                    "§eCoins: §b" + coins + "\n" +  // Blue for coins balance
                    "§6----------------------------";  // Yellow border

            // Display the message to the player
            player.sendMessage(message);

        } else {
            // If the command is not used by a player, inform the sender
            sender.sendMessage("§cThis command can only be used by players.");  // Red error message
        }

        return true;
    }
}
