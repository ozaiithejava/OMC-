package ozaii.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import ozaii.apis.base.FactoryApi;

import java.util.Arrays;
import java.util.List;

public class ChatCommand extends VanillaCommand {
    private static FactoryApi settingsApi = new FactoryApi(); // API nesnesi bir kez oluşturuluyor

    private static final String CHAT_DISABLED_MESSAGE = ChatColor.RED + "Chat is currently disabled.";
    private static final String CHAT_ENABLED_MESSAGE = ChatColor.GREEN + "Chat is now enabled.";

    public ChatCommand() {
        super("chat");
        this.description = "Chat command to enable or disable chat";
        this.usageMessage = "/chat on/off";
        this.setPermission("chat.command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Komutun doğru bir şekilde yazıldığını kontrol edelim
        if (!testPermission(sender)) return true;

        // Argüman sayısını kontrol edelim
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /chat on/off");
            return false;
        }

        // Chat durumu ayarını kontrol et
        boolean isChatEnabled = isChatEnabled(); // Dinamik olarak chat durumunu kontrol et

        // /chat on veya /chat off işlemleri
        if (args[0].equalsIgnoreCase("on")) {
            if (!isChatEnabled) {
                settingsApi.getSettingsFactory().set("chatEnabled", "true"); // Chat'i açıyoruz
                sender.sendMessage(CHAT_ENABLED_MESSAGE);
            } else {
                sender.sendMessage(ChatColor.RED + "Chat is already enabled.");
            }
        } else if (args[0].equalsIgnoreCase("off")) {
            if (isChatEnabled) {
                settingsApi.getSettingsFactory().set("chatEnabled", "false"); // Chat'i kapatıyoruz
                sender.sendMessage(CHAT_DISABLED_MESSAGE);
            } else {
                sender.sendMessage(ChatColor.RED + "Chat is already disabled.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /chat on/off");
            return false;
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            // args[0] için "on" veya "off" seçeneklerini öneriyoruz
            return Arrays.asList("on", "off");
        }
        return null;
    }

    // Chat'in aktif olup olmadığını kontrol etmek için metot
    public static boolean isChatEnabled() {
        // API'den chatEnabled değerini alıyoruz ve kontrol ediyoruz
        String chatStatus = settingsApi.getSettingsFactory().get("chatEnabled");
        return chatStatus != null && chatStatus.equals("true");
    }

}
