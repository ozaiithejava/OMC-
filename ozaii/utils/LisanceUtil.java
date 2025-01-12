package ozaii.utils;

import ozaii.apis.base.FactoryApi;

import java.util.logging.Logger;

public class LisanceUtil {
    static FactoryApi api = new FactoryApi();

    private static final Logger logger = Logger.getLogger(new FactoryApi().getServerName());


    public static void checkLicense() {
        String ipAddress = IPAddressUtil.getIPv4Address();
        String licenseKey = api.getSettingsFactory().get("lisanceKey");
        System.out.println(licenseKey +" " + ipAddress);

        String result = api.getInstance().getLicenseFactory().validateLicense(ipAddress, licenseKey);
        if (result != null && !result.toString().equals("{\"success\":true,\"message\":\"Doğrulama başarılı.\"}")) {
            logger.warning("Lisans Dogrulanamadi kapaniyor");
            System.exit(0);
        }else {
            logger.info("Lisans Dogru");
        }
    }
}
