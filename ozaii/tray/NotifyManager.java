package ozaii.tray;

import ozaii.utils.OperatingSystemUtil;

import java.awt.*;
import java.io.IOException;

import static ozaii.utils.OperatingSystemUtil.detectCurrentOS;
import static ozaii.utils.OperatingSystemUtil.handleOperatingSystem;

public class NotifyManager {

    public void setupNotfiy(){
        // Geçerli işletim sistemini algıla
        OperatingSystemUtil currentOS = detectCurrentOS();

        // İşletim sistemi seçici çağrısı
        handleOperatingSystem(currentOS);

        // İşletim sistemi adı
        System.out.println("Algılanan İşletim Sistemi: " + currentOS.getName());
        if (currentOS != null && currentOS == OperatingSystemUtil.WINDOWS) {
            try {
                new NotificationTray();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
