package ozaii.tray;

import ozaii.utils.oes.OperatingSystemUtil;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static ozaii.utils.oes.OperatingSystemUtil.detectCurrentOS;
import static ozaii.utils.oes.OperatingSystemUtil.handleOperatingSystem;

public class NotifyManager {

    // Asynchronous setup method
    public CompletableFuture<Void> setupNotifyAsync() {
        return CompletableFuture.runAsync(() -> {
            // Geçerli işletim sistemini algıla
            OperatingSystemUtil currentOS = detectCurrentOS();

            // İşletim sistemi seçici çağrısı
            handleOperatingSystem(currentOS);

            // İşletim sistemi adı
            System.out.println("Algılanan İşletim Sistemi: " + currentOS.getName());

            // Windows işletim sistemi kontrolü
            if (currentOS != null && currentOS == OperatingSystemUtil.WINDOWS) {
                try {
                    new NotificationTray();
                } catch (AWTException e) {
                    throw new RuntimeException("Error creating notification tray: ", e);
                } catch (IOException e) {
                    throw new RuntimeException("Error initializing notification tray: ", e);
                }
            }
        });
    }
}
