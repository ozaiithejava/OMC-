package ozaii.utils.web;

import ozaii.apis.base.FactoryApi;

import java.util.logging.Logger;

public class LisanceUtil {
    private static final FactoryApi api = new FactoryApi();
    private static final Logger logger = Logger.getLogger(api.getServerName());

    public static void checkLicense() {
        String ipAddress = IPAddressUtil.getIPv4Address();
        String licenseKey = api.getSettingsFactory().get("lisanceKey");
        logger.info("Lisans anahtarı ve IP adresi kontrol ediliyor: " + licenseKey + " " + ipAddress);

        api.getInstance().getLicenseFactory().validateLicenseAsync(ipAddress, licenseKey)
                .thenAccept(result -> {
                    if (result == null || !result.equals("{\"success\":true,\"message\":\"Doğrulama başarılı.\"}")) {
                        logger.warning("Lisans doğrulanamadı, uygulama kapatılıyor.");
                        System.exit(0);
                    } else {
                        logger.info("Lisans doğrulandı.");
                    }
                })
                .exceptionally(ex -> {
                    logger.severe("Lisans doğrulama sırasında bir hata oluştu: " + ex.getMessage());
                    System.exit(0);
                    return null;
                });
    }
}
