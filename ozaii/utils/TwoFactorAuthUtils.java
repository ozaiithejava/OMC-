package ozaii.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TwoFactorAuthUtils {
    private static final Map<UUID, AuthCode> authCodes = new HashMap<>();
    private static final Random random = new Random();

    /**
     * Bir oyuncuya özel bir kod oluşturur.
     *
     * @param player    Kod atanacak oyuncu.
     * @param length    Kodun uzunluğu.
     * @param duration  Kodun geçerlilik süresi (milisaniye cinsinden).
     * @return Oluşturulan kod.
     */
    public static String generateCode(Player player, int length, long duration) {
        UUID playerUUID = player.getUniqueId();
        String code = generateRandomCode(length);
        long expiryTime = System.currentTimeMillis() + duration;

        authCodes.put(playerUUID, new AuthCode(code, expiryTime));
        return code;
    }

    /**
     * Bir oyuncunun kodunun geçerlilik süresini kontrol eder.
     *
     * @param player Kodun atanmış olduğu oyuncu.
     * @return Kod geçerli ise true, değilse false.
     */
    public static boolean isCodeValid(Player player) {
        UUID playerUUID = player.getUniqueId();
        AuthCode authCode = authCodes.get(playerUUID);

        if (authCode == null) {
            return false;
        }

        // Kodun süresi geçtiyse otomatik olarak sil
        if (System.currentTimeMillis() > authCode.getExpiryTime()) {
            authCodes.remove(playerUUID);
            return false;
        }

        return true;
    }

    /**
     * Bir oyuncuya atanmış kodu alır.
     *
     * @param player Kodun atanmış olduğu oyuncu.
     * @return Kod, yoksa null.
     */
    public static String getCode(Player player) {
        UUID playerUUID = player.getUniqueId();
        AuthCode authCode = authCodes.get(playerUUID);

        if (authCode == null) {
            return null;
        }

        // Kodun süresi geçtiyse otomatik olarak sil
        if (System.currentTimeMillis() > authCode.getExpiryTime()) {
            authCodes.remove(playerUUID);
            return null;
        }

        return authCode.getCode();
    }

    /**
     * Bir oyuncuya atanmış kodu manuel olarak siler.
     *
     * @param player Kodun atanmış olduğu oyuncu.
     */
    public static void removeCode(Player player) {
        UUID playerUUID = player.getUniqueId();
        authCodes.remove(playerUUID);
    }

    /**
     * Belirtilen uzunlukta rastgele bir kod oluşturur.
     *
     * @param length Kodun uzunluğu.
     * @return Oluşturulan kod.
     */
    private static String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10)); // 0-9 arasında bir sayı ekle
        }
        return code.toString();
    }

    /**
     * Oyuncunun kodunu ve geçerliliğini kontrol eder.
     *
     * @param player Oyuncu.
     * @param code   Girilen kod.
     * @return Kod geçerliyse true, geçerli değilse false.
     */
    public static boolean isCodeValidFromPlayer(Player player, String code) {
        UUID playerUUID = player.getUniqueId();
        AuthCode authCode = authCodes.get(playerUUID);

        if (authCode == null) {
            return false;
        }

        // Kod geçerli mi kontrol et
        if (authCode.getCode().equals(code) && System.currentTimeMillis() <= authCode.getExpiryTime()) {
            return true;
        }

        return false;
    }

    /**
     * Kod ve süresini tutmak için yardımcı sınıf.
     */
    private static class AuthCode {
        private final String code;
        private final long expiryTime;

        public AuthCode(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }

        public String getCode() {
            return code;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }
}
