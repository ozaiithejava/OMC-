package ozaii.tray;

import ozaii.apis.base.FactoryApi;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class NotificationTray {

    private TrayIcon trayIcon;
    private Properties configProperties;

    private static final String ICON_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTQJft4KIF1AyP9CMlLZyZkafmNjlgq14myOg&s"; // İkonun indirileceği URL
    private static final String ICON_PATH = "icons/icon.png"; // İkonun kaydedileceği dosya yolu

    /**
     * NotificationTray yapıcı metodu.
     * Sistem tepsisini başlatır, gerekli dosyaları oluşturur ve menü öğelerini ekler.
     */
    public NotificationTray() throws AWTException, IOException {
        // icons dizinini ve tray.png dosyasını oluştur
        setupIconsDirectory();

        // config.properties dosyasını yükle
        loadConfigProperties();

        // SystemTray kontrolü
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("Sistem tepsisi desteklenmiyor.");
        }

        // SystemTray ve TrayIcon yapılandırması
        SystemTray systemTray = SystemTray.getSystemTray();
        BufferedImage trayImage = ImageIO.read(new File(ICON_PATH));
        trayIcon = new TrayIcon(trayImage, "Bildirim Sistemi");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Bildirim Sistemi Aktif");

        // Menü öğelerini ekle
        PopupMenu popupMenu = createPopupMenu();
        trayIcon.setPopupMenu(popupMenu);

        // TrayIcon'u sisteme ekle
        systemTray.add(trayIcon);
    }

    /**
     * icons dizinini ve tray.png dosyasını oluşturur.
     */
    private void setupIconsDirectory() throws IOException {
        File iconsDir = new File("icons");
        if (!iconsDir.exists() && iconsDir.mkdir()) {
            System.out.println("icons dizini oluşturuldu.");
        }

        File iconFile = new File(ICON_PATH);
        if (!iconFile.exists()) {
            System.out.println("İkon bulunamadı, indiriliyor...");
            downloadIcon();
        }
    }

    /**
     * İkonu belirtilen URL'den indirir ve icons dizinine kaydeder.
     */
    private void downloadIcon() {
        try (InputStream inputStream = new URL(ICON_URL).openStream();
             FileOutputStream fileOutputStream = new FileOutputStream(ICON_PATH)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("İkon başarıyla indirildi.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "İkon dosyası indirilemedi!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * config.properties dosyasını yükler. Eğer dosya yoksa, varsayılan olarak oluşturur.
     */
    private void loadConfigProperties() {
        configProperties = new Properties();
        File configFile = new File("config.properties");

        // Eğer config.properties dosyası yoksa, varsayılan olarak oluştur
        if (!configFile.exists()) {
            System.out.println("config.properties dosyası bulunamadı. Varsayılan dosya oluşturuluyor.");
            try {
                // Varsayılan değerler
                configProperties.setProperty("discord.url", "www.ozaiidev.com.tr/discord");
                configProperties.setProperty("github.url", "https://github.com/ozaiithejava");
                configProperties.setProperty("website.url", "wwww.ozaiidev.com.tr");

                // Dosyayı oluştur ve varsayılan değerleri kaydet
                try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
                    configProperties.store(outputStream, "Config file created with default values");
                    System.out.println("config.properties dosyası oluşturuldu.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "config.properties dosyası oluşturulamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Dosya varsa, yükle
            try (FileInputStream inputStream = new FileInputStream(configFile)) {
                configProperties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "config.properties dosyası yüklenemedi!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Sistem tepsisi menüsünü oluşturur.
     */
    private PopupMenu createPopupMenu() {
        PopupMenu popupMenu = new PopupMenu();

        // Bildirim Göster
        MenuItem showNotificationItem = new MenuItem(new FactoryApi().getServerName());
        //showNotificationItem.addActionListener(event -> showNotification("Bildirim", "Merhaba! Sistem bildirimi çalışıyor."));
        popupMenu.add(showNotificationItem);

        // Discord'a yönlendirme
        MenuItem discordItem = new MenuItem("Discord");
        discordItem.addActionListener(event -> openUrl(configProperties.getProperty("discord.url", "https://discord.com")));
        popupMenu.add(discordItem);

        // GitHub'a yönlendirme
        MenuItem githubItem = new MenuItem("GitHub");
        githubItem.addActionListener(event -> openUrl(configProperties.getProperty("github.url", "https://github.com")));
        popupMenu.add(githubItem);

        // Web sitesine yönlendirme
        MenuItem websiteItem = new MenuItem("Web Site");
        websiteItem.addActionListener(event -> openUrl(configProperties.getProperty("website.url", "https://example.com")));
        popupMenu.add(websiteItem);

        // Çıkış
        MenuItem exitItem = new MenuItem("Çıkış");
        exitItem.addActionListener(event -> shutdown());
        popupMenu.add(exitItem);

        return popupMenu;
    }

    /**
     * Bildirim gönderir.
     *
     * @param title   Bildirim başlığı.
     * @param message Bildirim mesajı.
     */
    private void showNotification(String title, String message) {
        SwingUtilities.invokeLater(() -> trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO));
    }

    /**
     * Tarayıcıda bir URL açar.
     *
     * @param url Açılacak URL.
     */
    private void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tepsiyi kapatır ve uygulamayı sonlandırır.
     */
    private void shutdown() {
        SystemTray.getSystemTray().remove(trayIcon);
        System.exit(0);
    }
}
