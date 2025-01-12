package ozaii.commands.admin;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.draw.Draw;
import ozaii.draw.DrawManager;

import java.util.ArrayList;
import java.util.List;

public class CekilisCommand extends VanillaCommand {

    private static DrawManager drawManager = new DrawManager(); // Çekiliş yöneticisi

    public CekilisCommand() {
        super("cekilis");
        this.description = "Çekiliş işlemleri yönetir.";
        this.usageMessage = "/cekilis <create|delete|list|katıl|sonlandır> ...";
        this.setPermission("bukkit.cekilis");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // "create", "delete", "sonlandır" komutları için yetki kontrolü
        if ((args.length > 0 && ("create".equalsIgnoreCase(args[0]) || "delete".equalsIgnoreCase(args[0]) || "sonlandır".equalsIgnoreCase(args[0]))) && !testPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Eksik argüman! Kullanım: " + usageMessage);
            return false;
        }

        String subCommand = args[0];

        switch (subCommand.toLowerCase()) {
            case "create":
                return createDraw(sender, args);
            case "delete":
                return deleteDraw(sender, args);
            case "list":
                return listDraws(sender);
            case "katıl":
                return joinDraw(sender, args);
            case "sonlandır":
                return endDraw(sender, args);
            default:
                sender.sendMessage(ChatColor.RED + "Geçersiz komut!");
                return false;
        }
    }

    private boolean createDraw(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Eksik argüman! Kullanım: /cekilis create <hediyeBaşlığı> <kazananSayısı> <kaçdk>");
            return false;
        }

        String prize = args[1];
        int winnerCount = parseInt(args[2], sender);
        long duration = parseDuration(args[3], sender);

        if (winnerCount <= 0 || duration <= 0) {
            return false;
        }

        // Çekilişi oluştur
        Draw draw = drawManager.createDraw(prize, winnerCount, duration);
        draw.start();

        sender.sendMessage(ChatColor.GREEN + "Çekiliş başarıyla başlatıldı! Hediyeniz: " + prize);
        return true;
    }

    private boolean deleteDraw(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Eksik argüman! Kullanım: /cekilis delete <id>");
            return false;
        }

        int id = parseInt(args[1], sender);
        if (id <= 0 || drawManager.getDraw(id) == null) {
            sender.sendMessage(ChatColor.RED + "Geçersiz çekiliş ID'si.");
            return false;
        }

        drawManager.removeDraw(id);
        sender.sendMessage(ChatColor.GREEN + "Çekiliş başarıyla silindi.");
        return true;
    }

    private boolean listDraws(CommandSender sender) {
        if (drawManager.getDrawMap().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Hiç çekiliş bulunmamaktadır.");
            return false;
        }

        sender.sendMessage(ChatColor.GREEN + "Mevcut Çekilişler:");
        for (Draw draw : drawManager.getDrawMap().values()) {
            sender.sendMessage(ChatColor.YELLOW + "ID: " + draw.getId() + " - " + draw.getPrize());
        }

        return true;
    }

    // Katılma komutu
    private boolean joinDraw(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Bu komut yalnızca oyuncular tarafından kullanılabilir.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Eksik argüman! Kullanım: /cekilis katıl <id>");
            return false;
        }

        int id = parseInt(args[1], sender);
        Draw draw = drawManager.getDraw(id);

        if (draw == null) {
            sender.sendMessage(ChatColor.RED + "Geçersiz çekiliş ID'si.");
            return false;
        }

        // Çekilişe katılma
        if (draw.addParticipant(player)) {
            sender.sendMessage(ChatColor.GREEN + "Çekilişe başarıyla katıldınız! Hediyeniz: " + draw.getPrize());
        } else {
            sender.sendMessage(ChatColor.RED + "Çekilişe katılamadınız. Çekiliş sona erdi ya da zaten katıldınız.");
        }

        return true;
    }

    // Çekilişi sonlandırma komutu
    private boolean endDraw(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Eksik argüman! Kullanım: /cekilis sonlandır <id>");
            return false;
        }

        int id = parseInt(args[1], sender);
        Draw draw = drawManager.getDraw(id);

        if (draw == null) {
            sender.sendMessage(ChatColor.RED + "Geçersiz çekiliş ID'si.");
            return false;
        }

        if (draw.isExpired()) {
            sender.sendMessage(ChatColor.RED + "Bu çekiliş zaten sona erdi.");
            return false;
        }

        // Çekilişi sonlandır ve kazananları açıklama
        draw.announceWinners();
        drawManager.removeDraw(id);
        sender.sendMessage(ChatColor.GREEN + "Çekiliş başarıyla sonlandırıldı ve kazananlar açıklandı.");
        return true;
    }

    // Argümanları int'e çevirme
    private int parseInt(String arg, CommandSender sender) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Geçersiz bir sayı girdiniz.");
            return -1;
        }
    }

    // Argümanları süreye çevirmek (dakika olarak)
    private long parseDuration(String arg, CommandSender sender) {
        try {
            return Long.parseLong(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Geçersiz bir süre girdiniz.");
            return -1;
        }
    }

    // Tab Tamamlama
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        List<String> completions = new ArrayList<>();

        // Eğer ilk argüman "create", "delete", "list", "katıl" veya "sonlandır" ise, tamamlamaları öner
        if (args.length == 1) {
            if ("create".startsWith(args[0].toLowerCase())) {
                completions.add("create");
            }
            if ("delete".startsWith(args[0].toLowerCase())) {
                completions.add("delete");
            }
            if ("list".startsWith(args[0].toLowerCase())) {
                completions.add("list");
            }
            if ("katıl".startsWith(args[0].toLowerCase())) {
                completions.add("katıl");
            }
            if ("sonlandır".startsWith(args[0].toLowerCase())) {
                completions.add("sonlandır");
            }
        }

        // Eğer "katıl" komutundaysak ve ikinci argüman id olmalı, mevcut çekiliş ID'lerini tamamlayalım
        if (args.length == 2 && args[0].equalsIgnoreCase("katıl")) {
            for (Draw draw : drawManager.getDrawMap().values()) {
                if (!draw.isExpired()) { // Sadece aktif çekilişler
                    completions.add(String.valueOf(draw.getId()));
                }
            }
        }

        // Eğer "sonlandır" komutundaysak ve ikinci argüman id olmalı, mevcut çekiliş ID'lerini tamamlayalım
        if (args.length == 2 && args[0].equalsIgnoreCase("sonlandır")) {
            for (Draw draw : drawManager.getDrawMap().values()) {
                if (!draw.isExpired()) { // Sadece aktif çekilişler
                    completions.add(String.valueOf(draw.getId()));
                }
            }
        }

        return completions.isEmpty() ? ImmutableList.of() : completions;
    }
}
