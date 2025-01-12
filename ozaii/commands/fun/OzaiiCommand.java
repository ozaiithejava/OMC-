package ozaii.commands.fun;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class OzaiiCommand extends VanillaCommand {

    public OzaiiCommand() {
        super("ozaii");
        this.description = "Aşırı güçlü bir elmas zırh seti ve elmas kılıç verir, yıldırım çarptırır.";
        this.usageMessage = "/ozaii";
        this.setPermission("admin.command.ozaii");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Yetki kontrolü
        if (!testPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok.");
            return false;
        }

        // Eğer bir oyuncu komutu çalıştırıyorsa
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Elmas zırh seti oluşturuyoruz
            ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
            ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
            ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
            ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);

            // Elmas Kılıç
            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta swordMeta = sword.getItemMeta();
            if (swordMeta != null) {
                swordMeta.setDisplayName(ChatColor.RED + "Özel Elmas Kılıç");
                swordMeta.setLore(Arrays.asList(ChatColor.GRAY + "Antik Krallık Ozaii'nin XX yy'si"));
                swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true); // Keskinlik V
                swordMeta.addEnchant(Enchantment.FIRE_ASPECT, 2, true); // Ateş Aspekti II
                sword.setItemMeta(swordMeta);
            }

            // Zırh setlerini ve kılıcı özelleştiriyoruz
            ItemMeta helmetMeta = helmet.getItemMeta();
            if (helmetMeta != null) {
                helmetMeta.setDisplayName(ChatColor.RED + "Antik Krallık Kaskı");
                helmetMeta.setLore(Arrays.asList(ChatColor.GRAY + "Antik Krallık Ozaii'nin XX yy'si"));
                helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true); // Koruma IV
                helmet.setItemMeta(helmetMeta);
            }

            ItemMeta chestplateMeta = chestplate.getItemMeta();
            if (chestplateMeta != null) {
                chestplateMeta.setDisplayName(ChatColor.RED + "Antik Krallık Zırhı");
                chestplateMeta.setLore(Arrays.asList(ChatColor.GRAY + "Antik Krallık Ozaii'nin XX yy'si"));
                chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true); // Koruma IV
                chestplateMeta.addEnchant(Enchantment.THORNS, 3, true); // Dikeni III
                chestplate.setItemMeta(chestplateMeta);
            }

            ItemMeta leggingsMeta = leggings.getItemMeta();
            if (leggingsMeta != null) {
                leggingsMeta.setDisplayName(ChatColor.RED + "Antik Krallık Pantolonu");
                leggingsMeta.setLore(Arrays.asList(ChatColor.GRAY + "Antik Krallık Ozaii'nin XX yy'si"));
                leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true); // Koruma IV
                leggingsMeta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true); // Derinlik Yürüyüşü III
                leggings.setItemMeta(leggingsMeta);
            }

            ItemMeta bootsMeta = boots.getItemMeta();
            if (bootsMeta != null) {
                bootsMeta.setDisplayName(ChatColor.RED + "Antik Krallık Botları");
                bootsMeta.setLore(Arrays.asList(ChatColor.GRAY + "Antik Krallık Ozaii'nin XX yy'si"));
                bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true); // Koruma IV
                bootsMeta.addEnchant(Enchantment.PROTECTION_FALL, 4, true); // Yüksek Düşüş IV
                boots.setItemMeta(bootsMeta);
            }

            // Zırh setini ve kılıcı oyuncuya veriyoruz
            player.getInventory().addItem(sword);

            // Oyuncuya zırhı giydiriyoruz
            player.getInventory().setHelmet(helmet);
            player.getInventory().setChestplate(chestplate);
            player.getInventory().setLeggings(leggings);
            player.getInventory().setBoots(boots);

            // Oyuncuya büyüleri uyguluyoruz
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE, 600, 1)); // Güç II
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, 600, 1)); // Direnç II
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.ABSORPTION, 600, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));

            // Yıldırım çarpmasını sağlıyoruz
            player.getWorld().strikeLightning(player.getLocation()); // Yıldırım çarptır
            player.setHealth(20);
            player.setFoodLevel(20);

            player.sendMessage(ChatColor.GREEN + "Aşırı güçlü büyülü bir elmas seti ve elmas kılıç aldınız! Üzerinize yıldırım çarptı!");

            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Bu komut sadece oyuncular tarafından kullanılabilir.");
            return false;
        }
    }
}
